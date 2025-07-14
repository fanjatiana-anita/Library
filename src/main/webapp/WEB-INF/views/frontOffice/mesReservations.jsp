<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Mes Réservations</title>
</head>
<body>
    <h1>Mes Réservations</h1>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <c:if test="${empty reservations}">
        <p>Aucune réservation trouvée.</p>
    </c:if>
    <c:if test="${not empty reservations}">
        <table border="1">
            <tr>
                <th>ID Réservation</th>
                <th>Titre du livre</th>
                <th>Date de réservation</th>
                <th>Date du prêt prévu</th>
                <th>Statut</th>
                <th>Action</th>
            </tr>
            <c:forEach var="reservation" items="${reservations}">
                <tr>
                    <td>${reservation.idReservation}</td>
                    <td>${reservation.exemplaire.livre.titreLivre}</td>
                    <td>${reservation.dateDeReservation}</td>
                    <td>${reservation.dateDuPretPrevue}</td>
                    <td>${reservation.statutReservation}</td>
                    <td><a href="${pageContext.request.contextPath}/frontoffice/detailsReservation?idReservation=${reservation.idReservation}">Voir détails</a></td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
    <p><a href="${pageContext.request.contextPath}/frontoffice/accueil">Retour à l'accueil</a></p>
</body>
</html>
