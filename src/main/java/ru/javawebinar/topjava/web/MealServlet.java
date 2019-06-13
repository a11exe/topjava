package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealDAO;
import ru.javawebinar.topjava.util.MealDAOInMemory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {

    private static final Logger log = getLogger(MealServlet.class);
    private MealDAO dao;

    public MealServlet() {
        dao = new MealDAOInMemory();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("redirect to meals");

        String action = request.getParameter("action");

        if (action != null) {
            if (action.equalsIgnoreCase("delete")) {
                int mealId = Integer.parseInt(request.getParameter("mealId"));
                dao.delete(mealId);
                request.setAttribute("meals", dao.getAllMeals());
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
            } else if (action.equalsIgnoreCase("add")) {
                request.setAttribute("meal", new Meal(LocalDateTime.now(), "new meal", 200));
                request.getRequestDispatcher("/meal.jsp").forward(request, response);
            } else if (action.equalsIgnoreCase("edit")) {
                int mealId = Integer.parseInt(request.getParameter("mealId"));
                request.setAttribute("meal", dao.getById(mealId));
                request.getRequestDispatcher("/meal.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("meals", dao.getAllMeals());
            request.getRequestDispatcher("/meals.jsp").forward(request, response);
        }
        //response.sendRedirect("meals.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(Integer.parseInt(request.getParameter("id")), LocalDateTime.parse(request.getParameter("dateTime")), request.getParameter("description"), Integer.parseInt(request.getParameter("calories")));

        if (meal == null || meal.getId() == 0) {
            dao.add(meal);
        } else {
            dao.update(meal);
        }
        response.sendRedirect("meals");
    }

}
