<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>

<div class="container">
    <p class="h2">Meals</p>
    <table class="table table-striped">
        <thead>
        <tr>
            <th scope="col">id</th>
            <th scope="col">Description</th>
            <th scope="col">Date</th>
            <th scope="col">Calories</th>
            <th scope="col">edit</th>
            <th scope="col">delete</th>
        </tr>
        </thead>
        <tbody>
            <c:forEach var="meal" items="${meals}">
                <tr class=${meal.isExcess() == true ?  '"text-danger"': '"text"'}>
                    <td>${meal.getId()}</td>
                    <td>${meal.getDescription()}</td>
                    <td>${meal.getDateTime().toString().replace("T", " ")}</td>
                    <td>${meal.getCalories()}</td>
                    <td><a href = "<c:url value = "?action=edit&mealId=${meal.getId()}"/>">Edit</a></td>
                    <td><a href = "<c:url value = "?action=delete&mealId=${meal.getId()}"/>">X</a></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <a href="?action=add" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Add meal</a>
</div>

</body>
</html>