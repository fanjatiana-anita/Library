<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Accueil Adhérent</title>
</head>
<body>
    <h2>Bienvenue, ${sessionScope.currentUser.personne.nomPersonne}</h2>
    <p><a href="${pageContext.request.contextPath}/frontoffice/reservations">Réserver un livre</a></p>
    <p><a href="${pageContext.request.contextPath}/logout">Se déconnecter</a></p>
</body>
</html>