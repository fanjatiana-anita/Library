<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Réabonnement d'un adhérent</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">

</head>
<body>
    <h2>Réabonnement d'un adhérent</h2>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <c:if test="${not empty success}">
        <p style="color: green;">${success}</p>
    </c:if>
    <form action="${pageContext.request.contextPath}/backoffice/reabonnement" method="post">
        <h3>Informations de réabonnement</h3>
        <label>Login de l'adhérent: </label><input type="text" name="login" required><br>
        <label>Date de paiement: </label>
        <input type="date" name="datePaiement" value="${today}" required><br>
        <label>Date de fin d'abonnement (facultatif): </label>
        <input type="date" name="dateFinAbonnement"><br>
        <input type="submit" value="Réabonner">
        <a href="${pageContext.request.contextPath}/backoffice/dashboard">Retour au tableau de bord</a>
    </form>
</body>
</html>