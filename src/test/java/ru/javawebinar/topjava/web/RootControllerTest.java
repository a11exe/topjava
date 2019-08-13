package ru.javawebinar.topjava.web;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import ru.javawebinar.topjava.TestUtil;
import ru.javawebinar.topjava.to.MealTo;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javawebinar.topjava.MealTestData.MEALS;
import static ru.javawebinar.topjava.TestUtil.mockAuthorize;
import static ru.javawebinar.topjava.TestUtil.userAuth;
import static ru.javawebinar.topjava.UserTestData.ADMIN;
import static ru.javawebinar.topjava.UserTestData.USER;
import static ru.javawebinar.topjava.util.MealsUtil.getWithExcess;

class RootControllerTest extends AbstractControllerTest {

    @Test
    void getUsers() throws Exception {
        mockMvc.perform(get("/users")
                .with(userAuth(ADMIN)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/users.jsp"));
    }

    @Test
    void unAuth() throws Exception {
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void getMeals() throws Exception {
        mockAuthorize(USER);
        List<MealTo> userMeals = getWithExcess(MEALS, SecurityUtil.authUserCaloriesPerDay());
        mockMvc.perform(get("/meals")
                .with(userAuth(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("meals"))
                .andExpect(model().attribute("meals", userMeals))
                .andExpect(forwardedUrl("/WEB-INF/jsp/meals.jsp"));
    }
}