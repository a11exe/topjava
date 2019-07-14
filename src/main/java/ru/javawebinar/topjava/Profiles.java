package ru.javawebinar.topjava;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.web.meal.MealRestController;

public class Profiles {
    private static final Logger log = LoggerFactory.getLogger(Profiles.class);

    public static final String
            JDBC = "jdbc",
            JPA = "jpa",
            DATAJPA = "datajpa";

    public static final String REPOSITORY_IMPLEMENTATION = DATAJPA;

    public static final String
            POSTGRES_DB = "postgres",
            HSQL_DB = "hsqldb";

    //  Get DB profile depending of DB driver in classpath
    public static String getActiveDbProfile() {
        try {
            Class.forName("org.postgresql.Driver");
            log.info("active db profile {}", POSTGRES_DB);
            return POSTGRES_DB;
        } catch (ClassNotFoundException ex) {
            try {
                Class.forName("org.hsqldb.jdbcDriver");
                log.info("active db profile {}", HSQL_DB);
                return Profiles.HSQL_DB;
            } catch (ClassNotFoundException e) {
                log.info("active db profile not found");
                throw new IllegalStateException("Could not find DB driver");
            }
        }
    }
}
