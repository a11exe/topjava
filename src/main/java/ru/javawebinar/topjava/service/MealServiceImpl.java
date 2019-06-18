package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.Collection;

@Service
public class MealServiceImpl implements MealService {

    private MealRepository repository;

    @Autowired
    public MealServiceImpl(MealRepository repository) {
        this.repository = repository;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        Meal saved = repository.save(meal, userId);
        if (saved == null) {
            throw new NotFoundException(String.format("cant't save meal %s user id: %s", meal, userId));
        }
        return  saved;
    }

    @Override
    public boolean delete(int id, int userId) {
        boolean result = repository.delete(id, userId);
        if (!result) {
            throw new NotFoundException(String.format("cant't delete meal with id %s user id: %s", id, userId));
        }
        return  result;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = repository.get(id, userId);
        if (meal == null) {
            throw new NotFoundException(String.format("cant't get meal with id %s user id: %s", id, userId));
        }
        return  meal;
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        return repository.getAll(userId);
    }
}