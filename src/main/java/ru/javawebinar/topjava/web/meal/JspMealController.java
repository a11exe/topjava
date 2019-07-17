package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 17.07.2019
 */

@Controller
public class JspMealController {
    @Autowired
    private MealService service;

    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);

    @GetMapping("/meals")
    public String root(HttpServletRequest request, Model model) {

        String action = request.getParameter("action");
        String idStr = request.getParameter("id");
        Integer id = idStr == null ? null : Integer.parseInt(idStr);
        int userId = SecurityUtil.authUserId();

        if ("delete".equals(action)) {
           delete(id);
        } else if ("create".equals(action) || "update".equals(action)) {
            final Meal meal = "create".equals(action) ?
                    new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                    service.get(id, userId);
            model.addAttribute("meal", meal);
            return "mealForm";
        }

        model.addAttribute("meals", MealsUtil.getWithExcess(service.getAll(userId), SecurityUtil.authUserCaloriesPerDay()));

        return "meals";
    }

    @PostMapping("/meals")
    public String save(HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        int userId = SecurityUtil.authUserId();
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        log.info("id value: {}", request.getParameter("id"));

        if (StringUtils.isEmpty(request.getParameter("id"))) {
            service.create(meal, userId);
        } else {
            meal.setId(Integer.parseInt(request.getParameter("id")));
            log.info("meal id value: {}", meal.getId());
            service.update(meal, userId);
        }
        return ("redirect:meals");
    }

    public void delete(int id) {
        int userId = SecurityUtil.authUserId();
        service.delete(id, userId);
    }
}
