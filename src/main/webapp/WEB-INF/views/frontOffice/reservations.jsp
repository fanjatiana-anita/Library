<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Réserver un livre</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <h1>Liste des livres disponibles</h1>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <table border="1">
        <tr>
            <th>Titre</th>
            <th>Auteur</th>
            <th>Action</th>
        </tr>
        <c:forEach var="livre" items="${livres}">
            <tr>
                <td>${livre.titreLivre}</td>
                <td>${livre.auteur.personne.nomPersonne}</td>
                <td><a href="${pageContext.request.contextPath}/frontoffice/formReservation?idLivre=${livre.idLivre}">Réserver</a></td>
            </tr>
        </c:forEach>
    </table>
    <p><a href="${pageContext.request.contextPath}/frontoffice">Retour à l'accueil</a></p>
</body>
</html>
