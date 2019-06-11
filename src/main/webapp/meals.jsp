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
            <th scope="col">Description</th>
            <th scope="col">Date</th>
            <th scope="col">Calories</th>
        </tr>
        </thead>
        <tbody>
            <c:forEach var="meal" items="${meals}">
                <c:choose>
                    <c:when test="${meal.isExcess()}">
                        <tr class="text-danger">
                    </c:when>
                    <c:otherwise>
                        <tr>
                    </c:otherwise>
                </c:choose>
                    <td>${meal.getDescription()}</td>
                    <td>${meal.getDateTime().toString().replace("T", " ")}</td>
                    <td>${meal.getCalories()}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

</body>
</html>