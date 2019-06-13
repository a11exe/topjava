package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 11.06.2019
 */
public class MealDAOInMemory implements MealDAO {

    private static final CopyOnWriteArrayList<Meal> meals = new CopyOnWriteArrayList(Arrays.asList(
            new Meal(1, LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
            new Meal(2, LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
            new Meal(3, LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
            new Meal(4, LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
            new Meal(5, LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
            new Meal(6, LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
    ));


    @Override
    public CopyOnWriteArrayList<MealTo> getAllMeals() {
        return new CopyOnWriteArrayList(MealsUtil.getFilteredWithExcess(meals, null, null, 2000));
    }

    @Override
    public void delete(int mealId) {
        Meal toDelete = getById(mealId);
        meals.remove(toDelete);
    }

    @Override
    public Meal getById(int mealId) {
        return meals.stream().filter(m->m.getId() == mealId).findFirst().get();
    }

    @Override
    public void add(Meal meal) {
        meal.setId(meals.get(meals.size()-1).getId() + 1);
        meals.add(meal);
    }

    @Override
    public void update(Meal meal) {
        meals.set(meals.indexOf(getById(meal.getId())), meal);
    }
}
