<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Détails de la Demande de Prolongement</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <h1>Détails de la Demande de Prolongement</h1>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <c:if test="${not empty message}">
        <p style="color: green;">${message}</p>
    </c:if>
    <p><strong>Titre du livre :</strong> ${prolongementDisplay.prolongement.pret.exemplaire.livre.titreLivre}</p>
    <p><strong>Date de demande :</strong> <fmt:formatDate value="${prolongementDisplay.dateDemandeProlongementAsDate}" pattern="yyyy-MM-dd"/></p>
    <p><strong>Nouvelle date de retour prévue :</strong> <fmt:formatDate value="${prolongementDisplay.dateRetourPrevueApresProlongementAsDate}" pattern="yyyy-MM-dd"/></p>
    <p><strong>Statut :</strong> ${prolongementDisplay.prolongement.statutProlongement}</p>
    <a href="${pageContext.request.contextPath}/frontoffice/mesPrets">Retour à la liste des prêts</a>
</body>
</html>