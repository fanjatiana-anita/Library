<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Détails du prêt</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h1>Détails du prêt</h1>
    <c:if test="${not empty success}">
        <p style="color: green;">${success}</p>
    </c:if>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <c:if test="${not empty message}">
        <p style="color: red;"><strong>Information:</strong> ${message}</p>
    </c:if>
    <p><strong>Adhérent:</strong> ${personne.nomPersonne}</p>
    <p><strong>Login:</strong> ${adherent.userAccount.login}</p>
    <p><strong>Livre:</strong> ${livre.titreLivre}</p>
    <p><strong>Exemplaire ID:</strong> ${exemplaire.idExemplaire}</p>
    <p><strong>Date du prêt:</strong> ${pret.dateDuPret}</p>
    <p><strong>Date de retour prévue:</strong> ${pret.dateDeRetourPrevue}</p>
    <br>
    <a href="${pageContext.request.contextPath}/backoffice/pret">Retour à la liste des livres</a>
    <br>
    <a href="${pageContext.request.contextPath}/backoffice/dashboard">Retour au tableau de bord</a>
</body>
</html>