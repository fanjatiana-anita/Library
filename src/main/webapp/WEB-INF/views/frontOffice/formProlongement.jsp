<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Demande de Prolongement</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <h1>Demande de Prolongement</h1>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <form action="${pageContext.request.contextPath}/frontoffice/processProlongement" method="post">
        <input type="hidden" name="idPret" value="${idPret}"/>
        <p>
            <label>Titre du livre :</label>
            <span>${livre.titreLivre}</span>
        </p>
        <p>
            <label>Date de retour prévue actuelle :</label>
           <span>${dateRetourPrevueActuelle}</span>
        </p>
        <p>
            <label>Date de demande de prolongement :</label>
            <input type="date" name="dateDemandeProlongement" value="${dateDemandeProlongement}" required/>
        </p>
        <p>
            <label>Nouvelle date de retour prévue (optionnel) :</label>
            <input type="date" name="dateRetourPrevueApresProlongement" value="${dateRetourPrevueApresProlongement}"/>
        </p>
        <button type="submit">Valider la demande</button>
    </form>
    <a href="${pageContext.request.contextPath}/frontoffice/mesPrets">Retour à la liste des prêts</a>
</body>
</html>