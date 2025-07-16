<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Détails du prêt</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h1>Détails du prêt</h1>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <c:if test="${not empty success}">
        <p style="color: green;">${success}</p>
    </c:if>
    <c:if test="${not empty message}">
        <p style="color: blue;">${message}</p>
    </c:if>

    <h2>Informations du prêt</h2>
    <table border="1">
        <tr>
            <th>ID Prêt</th>
            <td>${pretDisplay.pret.idPret}</td>
        </tr>
        <tr>
            <th>Adhérent</th>
            <td>${personne.nomPersonne} (${adherent.userAccount.login})</td>
        </tr>
        <tr>
            <th>Livre</th>
            <td>${livre.titreLivre}</td>
        </tr>
        <tr>
            <th>Exemplaire ID</th>
            <td>${exemplaire.idExemplaire}</td>
        </tr>
        <tr>
            <th>Date du prêt</th>
            <td>
                <p>${pretDisplay.dateDuPret}</p>
            </td>
        </tr>
        <tr>
            <th>Date de retour prévue</th>
            <td>
                <p>${pretDisplay.dateDeRetourPrevue}</p>
            </td>
        </tr>
        <c:if test="${pretDisplay.dateDeRetourReelle != null}">
            <tr>
                <th>Date de retour réelle</th>
                <td>
                    <p>${pretDisplay.dateDeRetourReelle}</p>
                </td>
            </tr>
        </c:if>
    </table>

    <br>
    <a href="${pageContext.request.contextPath}/backoffice/return">Retour à la liste des prêts</a>
    <br>
    <a href="${pageContext.request.contextPath}/backoffice/dashboard">Retour au tableau de bord</a>
</body>
</html>
