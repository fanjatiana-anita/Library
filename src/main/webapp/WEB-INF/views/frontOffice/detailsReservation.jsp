<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
    <title>Détails de la Réservation</title>
    <style>
        table {
            width: 50%;
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
<h2>Détails de la Réservation</h2>

<c:choose>
    <c:when test="${empty user}">
        <p class="error">Vous devez être connecté pour voir les détails de la réservation.</p>
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
        <c:set var="reservation" value="${reservationDisplay.reservation}"/>
        <table>
            <tr>
                <th>Livre</th>
                <td>${reservation.exemplaire.livre.titreLivre}</td>
            </tr>
            <tr>
                <th>Date de réservation</th>
                <td>
                    <c:choose>
                        <c:when test="${not empty reservationDisplay.dateDeReservationAsDate}">
                            <fmt:formatDate value="${reservationDisplay.dateDeReservationAsDate}" pattern="yyyy-MM-dd"/>
                        </c:when>
                        <c:otherwise>Non définie</c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <th>Date prévue du prêt</th>
                <td>
                    <c:choose>
                        <c:when test="${not empty reservationDisplay.dateDuPretPrevueAsDate}">
                            <fmt:formatDate value="${reservationDisplay.dateDuPretPrevueAsDate}" pattern="yyyy-MM-dd"/>
                        </c:when>
                        <c:otherwise>Non définie</c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <th>Statut</th>
                <td>${reservation.statutReservation}</td>
            </tr>
            <tr>
                <th>Informations</th>
                <td>
                    <c:if test="${reservation.isLateValidation}">
                        <span class="late">Validation en retard. Récupérez avant le
                            <fmt:formatDate value="${reservationDisplay.dateLimiteRecuperationAsDate}" pattern="yyyy-MM-dd"/>
                        </span>
                    </c:if>
                </td>
            </tr>
        </table>
        <br>
        <a href="${pageContext.request.contextPath}/frontoffice/mesReservations">Retour à mes réservations</a><br>
        <a href="${pageContext.request.contextPath}/frontoffice/accueil">Retour à l'accueil</a>
    </c:otherwise>
</c:choose>
</body>
</html>
