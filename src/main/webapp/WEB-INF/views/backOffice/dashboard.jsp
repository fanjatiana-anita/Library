<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Tableau de bord - Back Office</title>
</head>
<body>
    <h2>Bienvenue, ${user.personne.nomPersonne}</h2>
    <p><a href="${pageContext.request.contextPath}/backoffice/inscription">Inscrire un nouvel adhérent</a></p>
    <p><a href="${pageContext.request.contextPath}/backoffice/reabonnement">Réabonner un adhérent</a></p>
    <p><a href="${pageContext.request.contextPath}/logout">Déconnexion</a></p>
</body>
</html>