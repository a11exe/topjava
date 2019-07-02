package ru.javawebinar.topjava.service;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    @Autowired
    private MealService service;

    private static List<String> testLog = new ArrayList<>();

    @AfterClass
    public static void afterClass() {
        testLog.forEach(s -> System.out.println(s));
    }

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TestWatcher watcher = new TestWatcher() {

        private long start;

        @Override
        protected void starting(Description description) {
            System.out.println("starting " + description.getMethodName());
            start = System.currentTimeMillis();
        }

        @Override
        protected void finished(Description description) {
            System.out.println("finished");
            // Get elapsed time in seconds
            long elapsedTimeMillis = System.currentTimeMillis()-start;
            float elapsedTimeSec = elapsedTimeMillis/1000F;

            // Get elapsed time in minutes
            float elapsedTimeMin = elapsedTimeMillis/(60*1000F);
            String testInfo = String.format("test %s time %s min. %s sec.",  description.getMethodName(), elapsedTimeMin, elapsedTimeSec);
            testLog.add(testInfo);
            log.info(testInfo);
        }
    };

    @Test
    public void delete() throws Exception {
        service.delete(MEAL1_ID, USER_ID);
        assertMatchIgnoringFields(service.getAll(USER_ID), Arrays.asList(MEAL6, MEAL5, MEAL4, MEAL3, MEAL2), "user");
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotFound() throws Exception {
        service.delete(1, USER_ID);
    }

    @Test
    public void deleteNotOwn() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found entity with id=100002");
        service.delete(MEAL1_ID, ADMIN_ID);
    }

    @Test
    public void create() throws Exception {
        Meal newMeal = getCreated();
        Meal created = service.create(newMeal, USER_ID);
        newMeal.setId(created.getId());
        assertMatchIgnoringFields(newMeal, created, "user");
        assertMatchIgnoringFields(service.getAll(USER_ID), Arrays.asList(newMeal, MEAL6, MEAL5, MEAL4, MEAL3, MEAL2, MEAL1), "user");
    }

    @Test
    public void get() throws Exception {
        Meal actual = service.get(ADMIN_MEAL_ID, ADMIN_ID);
        assertMatchIgnoringFields(actual, ADMIN_MEAL1, "user");
    }

    @Test(expected = NotFoundException.class)
    public void getNotFound() throws Exception {
        service.get(1, USER_ID);
    }

    @Test(expected = NotFoundException.class)
    public void getNotOwn() throws Exception {
        service.get(MEAL1_ID, ADMIN_ID);
    }

    @Test
    public void update() throws Exception {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatchIgnoringFields(service.get(MEAL1_ID, USER_ID), updated, "user");
    }

    @Test(expected = NotFoundException.class)
    public void updateNotFound() throws Exception {
        service.update(MEAL1, ADMIN_ID);
    }

    @Test
    public void getAll() throws Exception {
        assertMatchIgnoringFields(service.getAll(USER_ID), MEALS, "user");
    }

    @Test
    public void getBetween() throws Exception {
        assertMatchIgnoringFields(service.getBetweenDates(
                LocalDate.of(2015, Month.MAY, 30),
                LocalDate.of(2015, Month.MAY, 30), USER_ID), Arrays.asList(MEAL3, MEAL2, MEAL1) , "user");
    }
}