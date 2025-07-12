<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Réservation enregistrée</title>
</head>
<body>
    <h2>Réservation enregistrée</h2>
    <p style="color: green;">${message}</p>
    <p><a href="${pageContext.request.contextPath}/frontoffice/reservations">Retour à la liste des livres</a></p>
    <p><a href="${pageContext.request.contextPath}/frontoffice/home">Retour à l'accueil</a></p>
</body>
</html>