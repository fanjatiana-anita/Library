<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
    <title>Mes Réservations</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            border: 1px solid black;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
        .error {
            color: red;
        }
        .success {
            color: green;
        }
        .late {
            color: orange;
        }
    </style>
</head>
<body>
<h2>Mes Réservations</h2>

<c:choose>
    <c:when test="${empty user}">
        <p class="error">Vous devez être connecté pour voir vos réservations.</p>
        <a href="${pageContext.request.contextPath}/login">Se connecter</a>
    </c:when>
    <c:otherwise>
        <p>Bienvenue, ${user.personne.nomPersonne} (${user.login})</p>
        <c:if test="${not empty error}">
            <p class="error">${error}</p>
        </c:if>
        <c:if test="${not empty message}">
            <p class="success">${message}</p>
        </c:if>
        <table>
            <tr>
                <th>Livre</th>
                <th>Date de réservation</th>
                <th>Date prévue du prêt</th>
                <th>Statut</th>
                <th>Informations</th>
            </tr>
            <c:forEach var="reservationMap" items="${reservations}">
                <c:set var="reservation" value="${reservationMap.reservation}"/>
                <tr>
                    <td>${reservation.exemplaire.livre.titreLivre}</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty reservationMap.dateDeReservationAsDate}">
                                <fmt:formatDate value="${reservationMap.dateDeReservationAsDate}" pattern="yyyy-MM-dd"/>
                            </c:when>
                            <c:otherwise>Non définie</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty reservationMap.dateDuPretPrevueAsDate}">
                                <fmt:formatDate value="${reservationMap.dateDuPretPrevueAsDate}" pattern="yyyy-MM-dd"/>
                            </c:when>
                            <c:otherwise>Non définie</c:otherwise>
                        </c:choose>
                    </td>
                    <td>${reservation.statutReservation}</td>
                    <td>
                        <c:if test="${reservation.isLateValidation}">
                            <span class="late">Validation en retard. Récupérez avant le
                                <fmt:formatDate value="${reservationMap.dateLimiteRecuperationAsDate}" pattern="yyyy-MM-dd"/>
                            </span>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <br>
        <a href="${pageContext.request.contextPath}/frontoffice/reservations">Faire une nouvelle réservation</a><br>
        <a href="${pageContext.request.contextPath}/frontoffice/accueil">Retour à l'accueil</a>
    </c:otherwise>
</c:choose>
</body>
</html>
