package ru.javawebinar.topjava.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.UserUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 21.08.2019
 */

@Component
public class UserFormValidator implements Validator {

    @Autowired
    protected UserService service;

    @Override
    public boolean supports(Class<?> aClass) {
        return UserTo.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserTo user = (UserTo) target;

        User userByEmail = service.getByEmail(user.getEmail());
        if (userByEmail.getId() != SecurityUtil.authUserId()) {
            errors.rejectValue("email", "User with this email already exists", "User with this email already exists");
            //errors.addError(new FieldError("userTo", "email", "User with this email already exists"));
        }

    }

    public void validateNew(Object target, Errors errors) {
        UserTo user = (UserTo) target;

        User userByEmail = service.getByEmail(user.getEmail());
        if (userByEmail != null) {
            errors.rejectValue("email", "User with this email already exists", "User with this email already exists");
            //errors.addError(new FieldError("userTo", "email", "User with this email already exists"));
        }

    }
}
