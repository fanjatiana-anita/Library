<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Réservation de livres</title>
</head>
<body>
    <h2>Réservation de livres</h2>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <h3>Liste des livres</h3>
    <ul>
        <c:forEach var="livre" items="${livres}">
            <li>
                ${livre.titreLivre} - <a href="${pageContext.request.contextPath}/frontoffice/exemplaires/${livre.idLivre}">Voir les exemplaires disponibles</a>
            </li>
        </c:forEach>
    </ul>
    <p><a href="${pageContext.request.contextPath}/frontoffice/home">Retour à l'accueil</a></p>
</body>
</html>