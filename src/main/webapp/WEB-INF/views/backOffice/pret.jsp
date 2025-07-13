<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Enregistrer un prêt</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h1>Enregistrer un prêt</h1>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <h2>Liste des livres</h2>
    <table border="1">
        <thead>
            <tr>
                <th>Titre</th>
                <th>Auteur</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="livre" items="${livres}">
                <tr>
                    <td>${livre.titreLivre}</td>
                    <td>${livre.auteur.personne.nomPersonne}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/backoffice/formPret?idLivre=${livre.idLivre}">Prêter</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <br>
    <a href="${pageContext.request.contextPath}/backoffice/dashboard">Retour au tableau de bord</a>
</body>
</html>