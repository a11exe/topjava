<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
    <title>Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>

<div class="container">
    <p class="h2">${meal.getId() != 0 ?  'Edit meal id: ': 'New meal'}
        ${meal.getId() != 0 ?  meal.getId() : '' }
    </p>


    <form method="POST">
        <div class="form-group">
            <label for="id">id</label>
            <input type="number" class="form-control" id="id" name="id" value="${meal.getId()}" readonly>
        </div>
        <div class="form-group">
            <label for="description">description</label>
            <input type="text" class="form-control" id="description" name="description" value="${meal.getDescription()}">
        </div>
        <div class="form-group">
            <label for="dateTime">dateTime</label>
            <input type="datetime-local" class="form-control" id="dateTime" name="dateTime" value="${meal.getDateTime()}">
        </div>
        <div class="form-group">
            <label for="calories">calories</label>
            <input type="number" class="form-control" id="calories" name="calories" value="${meal.getCalories()}">
        </div>
        <button type="submit" class="btn btn-primary">Submit</button>
    </form>

</div>

</body>
</html>
