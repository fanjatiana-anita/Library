<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Réservations Validées</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">

</head>
<body>
    <h1>Réservations Validées</h1>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <c:if test="${not empty message}">
        <p style="color: green;">${message}</p>
    </c:if>
    <table border="1">
        <tr>
            <th>ID Réservation</th>
            <th>Adhérent</th>
            <th>Livre</th>
            <th>Date de Réservation</th>
            <th>Date de Prêt Prévue</th>
            <th>Date de Validation</th>
            <th>Date Limite de Récupération</th>
            <th>Action</th>
        </tr>
        <c:forEach items="${reservations}" var="reservation">
            <tr>
                <td>${reservation.idReservation}</td>
                <td>${reservation.adherent.userAccount.personne.nomPersonne} (${reservation.adherent.userAccount.login})</td>
                <td>${reservation.exemplaire.livre.titreLivre}</td>
                <td>${reservation.dateDeReservation}</td>
                <td>${reservation.dateDuPretPrevue}</td>
                <td>${reservation.dateValidation}</td>
                <td>${reservation.dateLimiteRecuperation}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/backoffice/formPret?idLivre=${reservation.exemplaire.livre.idLivre}&idReservation=${reservation.idReservation}">Transformer en Prêt</a>
                </td>
            </tr>
        </c:forEach>
    </table>
    <p><a href="${pageContext.request.contextPath}/backoffice">Retour</a></p>
</body>
</html>