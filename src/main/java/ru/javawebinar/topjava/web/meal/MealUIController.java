package ru.javawebinar.topjava.web.meal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.StringJoiner;

@RestController
@RequestMapping("/ajax/profile/meals")
public class MealUIController extends AbstractMealController {

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MealTo> getAll() {
        return super.getAll();
    }


    @Override
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Meal get(@PathVariable("id") int id) {
        return super.get(id);
    }

    @Override
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

//    @PostMapping
//    @ResponseStatus(value = HttpStatus.NO_CONTENT)
//    public void createOrUpdate(@RequestParam Integer id,
//                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
//                               @RequestParam String description,
//                               @RequestParam int calories) {
//        Meal meal = new Meal(id, dateTime, description, calories);
//        if (meal.isNew()) {
//            super.create(meal);
//        }
//    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<String> createOrUpdate(@Valid MealTo mealTo, BindingResult result) {
        Meal meal = new Meal(mealTo.getId(), mealTo.getDateTime(), mealTo.getDescription(), mealTo.getCalories());
        if (result.hasErrors()) {
            StringJoiner joiner = new StringJoiner("<br>");
            result.getFieldErrors().forEach(
                    fe -> {
                        String msg = fe.getDefaultMessage();
                        if (msg != null) {
                            if (!msg.startsWith(fe.getField())) {
                                msg = fe.getField() + ' ' + msg;
                            }
                            joiner.add(msg);
                        }
                    });
            return ResponseEntity.unprocessableEntity().body(joiner.toString());
        }
        if (mealTo.isNew()) {
            super.create(meal);
        } else {
            super.update(meal, meal.id());
        }
        return ResponseEntity.ok().build();

    }

    @Override
    @GetMapping(value = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MealTo> getBetween(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalTime startTime,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) LocalTime endTime) {
        return super.getBetween(startDate, startTime, endDate, endTime);
    }
}