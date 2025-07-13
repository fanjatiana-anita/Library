`<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Tableau de bord - Bibliothèque</title>
</head>
<body>
    <h1>Tableau de bord</h1>
    <c:if test="${not empty message}">
        <p style="color: green;">${message}</p>
    </c:if>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <ul>
        <li><a href="${pageContext.request.contextPath}/backoffice/inscription">Inscription</a></li>
        <li><a href="${pageContext.request.contextPath}/backoffice/reabonnement">Réabonnement</a></li>
        <li><a href="${pageContext.request.contextPath}/backoffice/pret">Gestion des prêts</a></li>
        <li><a href="${pageContext.request.contextPath}/backoffice/return">Retour des prêts</a></li>
    </ul>
</body>
</html>