package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.util.concurrent.CopyOnWriteArrayList;

public interface MealDAO {
    CopyOnWriteArrayList<MealTo> getAllMeals();

    void delete(int mealId);

    Meal getById(int mealId);

    void add(Meal meal);

    void update(Meal meal);
}
