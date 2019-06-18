package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.Collection;

@Controller
public class MealRestController {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MealService service;

    public Collection<MealTo> getAll(int userId) {
        return MealsUtil.getWithExcess(service.getAll(userId), MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

    public Meal save(Meal meal, int userId) {
        return service.save(meal, userId);
    }

    boolean delete(int id, int userId) {
        return service.delete(id, userId);
    }

    Meal get(int id, int userId) {
        return service.get(id, userId);
    }

}