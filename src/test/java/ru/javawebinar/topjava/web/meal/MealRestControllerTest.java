package ru.javawebinar.topjava.web.meal;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.time.Month;

import static java.time.LocalDateTime.of;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.assertMatch;
import static ru.javawebinar.topjava.MealTestData.contentJson;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.TestUtil.*;
import static ru.javawebinar.topjava.UserTestData.*;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;
import static ru.javawebinar.topjava.util.MealsUtil.createWithExcess;
import static ru.javawebinar.topjava.util.MealsUtil.getWithExcess;

class MealRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = MealRestController.REST_URL + '/';

    @Autowired
    private MealService service;

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_MEAL_ID)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertMatch(readFromJsonMvcResult(result, Meal.class), ADMIN_MEAL1));
    }

    @Test
    void getUnauth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + MEAL1_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_MEAL_ID)
                .with(userHttpBasic(USER)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + MEAL1_ID)
                .with(userHttpBasic(USER)))
                .andExpect(status().isNoContent());
        assertMatch(service.getAll(START_SEQ), MEAL6, MEAL5, MEAL4, MEAL3, MEAL2);
    }

    @Test
    void deleteNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + ADMIN_MEAL_ID)
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void update() throws Exception {
        Meal updated = getUpdated();

        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated))
                .with(userHttpBasic(USER)))
                .andExpect(status().isNoContent());

        assertMatch(service.get(MEAL1_ID, START_SEQ), updated);
    }

    @Test
    void updateNotValid() throws Exception {
        Meal updated = getUpdated();
        updated.setCalories(0);

        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated))
                .with(userHttpBasic(USER)))
                .andExpect(status().isUnprocessableEntity());

        //assertMatch(service.get(MEAL1_ID, START_SEQ), updated);
    }

    @Test
    void createWithLocation() throws Exception {
        Meal created = getCreated();
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created))
                .with(userHttpBasic(ADMIN)));

        Meal returned = readFromJson(action, Meal.class);
        created.setId(returned.getId());

        assertMatch(returned, created);
        assertMatch(service.getAll(ADMIN_ID), ADMIN_MEAL2, created, ADMIN_MEAL1);
    }

    @Test
    void createWithExeptionDuplicateMealDate() throws Exception {
        Meal created = new Meal(null, of(2015, Month.JUNE, 1, 14, 0), "Админ ланч", 510);

        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created))
                .with(userHttpBasic(ADMIN)));

        Meal returned = readFromJson(action, Meal.class);
        created.setId(returned.getId());

        DataIntegrityViolationException thrown =
                assertThrows(DataIntegrityViolationException.class,
                        () -> service.getAll(ADMIN_ID));
        assertTrue(thrown.getCause().getCause().getMessage().contains("meals_unique_user_datetime_idx"));

    }

    @Test
    void getAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(getWithExcess(MEALS, USER.getCaloriesPerDay())));
    }

    @Test
    void filter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("startDate", "2015-05-30").param("startTime", "07:00")
                .param("endDate", "2015-05-31").param("endTime", "11:00")
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(contentJson(createWithExcess(MEAL4, true), createWithExcess(MEAL1, false)));
    }

    @Test
    void filterAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "filter?startDate=&endTime=")
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andExpect(contentJson(getWithExcess(MEALS, USER.getCaloriesPerDay())));
    }
}