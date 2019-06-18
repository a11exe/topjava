package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 18.06.2019
 */
public class UsersUtil {

    public static final List<User> USERS = Arrays.asList(
            new User("alex", "alex@mail.com", "weqweqr", Role.ROLE_USER),
            new User("bob", "bob@mail.com", "464641", Role.ROLE_USER),
            new User("tony", "tony@mail.com", "7fsdf7", Role.ROLE_USER),
            new User("admin", "admin@mail.com", "addd4564", Role.ROLE_ADMIN),
            new User("michel", "michel@mail.com", "7s8s7s", Role.ROLE_USER)
    );

}
