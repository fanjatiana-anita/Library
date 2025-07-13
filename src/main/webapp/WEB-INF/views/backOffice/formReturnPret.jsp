<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Formulaire de retour de prêt</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h1>Formulaire de retour de prêt</h1>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <c:if test="${not empty success}">
        <p style="color: green;">${success}</p>
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
                <fmt:formatDate value="${pretDisplay.dateDuPret}" pattern="yyyy-MM-dd"/>
            </td>
        </tr>
        <tr>
            <th>Date de retour prévue</th>
            <td>
                <fmt:formatDate value="${pretDisplay.dateDeRetourPrevue}" pattern="yyyy-MM-dd"/>
            </td>
        </tr>
    </table>

    <h2>Enregistrer le retour</h2>
    <form action="${pageContext.request.contextPath}/backoffice/processReturn" method="post">
        <input type="hidden" name="idPret" value="${pretDisplay.pret.idPret}">
        <p>
            <label for="dateDeRetourReelle">Date de retour réelle :</label>
            <input type="date" id="dateDeRetourReelle" name="dateDeRetourReelle" 
                   value="<fmt:formatDate value='${today}' pattern='yyyy-MM-dd'/>" required>
        </p>
        <p>
            <input type="submit" value="Confirmer le retour">
        </p>
    </form>

    <br>
    <a href="${pageContext.request.contextPath}/backoffice/return">Retour à la liste des prêts</a>
    <br>
    <a href="${pageContext.request.contextPath}/backoffice/dashboard">Retour au tableau de bord</a>
</body>
</html>
