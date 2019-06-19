package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.repository.inmemory.InMemoryMealRepositoryImpl;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.meal.MealRestController;
import ru.javawebinar.topjava.web.user.AdminRestController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    private MealRepository repository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        //repository = new InMemoryMealRepositoryImpl();

        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml")) {
            System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));
            repository = appCtx.getBean(InMemoryMealRepositoryImpl.class);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")),
                SecurityUtil.authUserId());

        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        repository.save(meal, SecurityUtil.authUserId());
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        action = action == null ? "all" : action;

        if ("login".equals(action)) {
            int userId = getId(request);
            log.info("Login {}", userId);
            SecurityUtil.setUserId(userId);
            action = "all";
        }

        switch (action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                repository.delete(id, SecurityUtil.authUserId());
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000, SecurityUtil.authUserId()) :
                        repository.get(getId(request), SecurityUtil.authUserId());
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "filter":
                LocalDateTime from = getDateTime(getDateFrom(request), getTimeFrom(request));
                LocalDateTime to = getDateTime(getDateTo(request), getTimeTo(request));

                request.setAttribute("meals",
                        MealsUtil.getFilteredWithExcess(
                                repository.getAll(SecurityUtil.authUserId()),
                                MealsUtil.DEFAULT_CALORIES_PER_DAY,
                                (s -> s.getDateTime().isBefore(to) && s.getDateTime().isAfter(from))));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                request.setAttribute("meals",
                        MealsUtil.getWithExcess(repository.getAll(SecurityUtil.authUserId()), MealsUtil.DEFAULT_CALORIES_PER_DAY));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

    private LocalDate getDateFrom(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("dateFrom"));
        if (paramId.isEmpty()) {
            paramId = "1900-01-01";
        }
        return LocalDate.parse(paramId, DateTimeFormatter.ISO_DATE);
    }

    private LocalDate getDateTo(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("dateTo"));
        if (paramId.isEmpty()) {
            paramId = "3900-01-01";
        }
        return LocalDate.parse(paramId, DateTimeFormatter.ISO_DATE);
    }

    private LocalTime getTimeFrom(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("timeFrom"));
        if (paramId.isEmpty()) {
            paramId = "00:00";
        }
        return LocalTime.parse(paramId, DateTimeFormatter.ISO_TIME);
    }

    private LocalTime getTimeTo(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("timeTo"));
        if (paramId.isEmpty()) {
            paramId = "23:59";
        }
        return LocalTime.parse(paramId, DateTimeFormatter.ISO_TIME);
    }

    private LocalDateTime getDateTime(LocalDate date, LocalTime time) {
        return LocalDateTime.of(date, time);
    }
}
