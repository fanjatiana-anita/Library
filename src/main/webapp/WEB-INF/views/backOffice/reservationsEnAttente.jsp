<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Réservations en attente</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <script>
        function validateDate(idReservation, dateDeReservation) {
            const dateValidationInput = document.getElementById('dateValidation_' + idReservation);
            const dateValidation = new Date(dateValidationInput.value);
            const dateDeRes = new Date(dateDeReservation);
            if (dateValidation < dateDeRes) {
                alert("La date de validation doit être postérieure ou égale à la date de réservation (" + dateDeReservation + ").");
                dateValidationInput.value = '${today}';
                return false;
            }
            return true;
        }
    </script>
</head>
<body>
    <h1>Réservations en attente</h1>
    
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <%-- Conversion LocalDate -> java.util.Date pour JSTL --%>
    <%
        java.util.Date dateLimiteRecuperationAsDate = null;
        if (request.getAttribute("dateLimiteRecuperation") != null) {
            dateLimiteRecuperationAsDate = java.sql.Date.valueOf((java.time.LocalDate)request.getAttribute("dateLimiteRecuperation"));
            request.setAttribute("dateLimiteRecuperationAsDate", dateLimiteRecuperationAsDate);
        }
    %>
    <c:if test="${not empty message}">
        <p style="color: ${isLateValidation ? 'orange' : 'green'};">
            ${message}
            <c:if test="${isLateValidation}">
                (Date limite de récupération : <fmt:formatDate value="${dateLimiteRecuperationAsDate}" pattern="yyyy-MM-dd"/>)
            </c:if>
        </p>
    </c:if>

    <c:choose>
        <c:when test="${empty reservations}">
            <p>Aucune réservation en attente.</p>
        </c:when>
        <c:otherwise>
            <table border="1">
                <thead>
                    <tr>
                        <th>ID Réservation</th>
                        <th>Adhérent</th>
                        <th>Livre</th>
                        <th>Date de Réservation</th>
                        <th>Date de Prêt Prévue</th>
                        <th>Date de Validation</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="reservation" items="${reservations}">
                        <tr>
                            <td>${reservation.idReservation}</td>
                            <td>${reservation.adherent.userAccount.personne.nomPersonne}</td>
                            <td>${reservation.exemplaire.livre.titreLivre}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty reservation.dateDeReservation}">
                                        <fmt:parseDate value="${reservation.dateDeReservation}" pattern="yyyy-MM-dd" var="parsedDateDeReservation"/>
                                        <fmt:formatDate value="${parsedDateDeReservation}" pattern="yyyy-MM-dd"/>
                                    </c:when>
                                    <c:otherwise>
                                        Non définie
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty reservation.dateDuPretPrevue}">
                                        <fmt:parseDate value="${reservation.dateDuPretPrevue}" pattern="yyyy-MM-dd" var="parsedDateDuPretPrevue"/>
                                        <fmt:formatDate value="${parsedDateDuPretPrevue}" pattern="yyyy-MM-dd"/>
                                    </c:when>
                                    <c:otherwise>
                                        Non définie
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/backoffice/validerReservation" method="post" 
                                      onsubmit="return validateDate('${reservation.idReservation}', '${reservation.dateDeReservation}')">
                                    <input type="hidden" name="idReservation" value="${reservation.idReservation}"/>
                                    <input type="date" id="dateValidation_${reservation.idReservation}" name="dateValidation" 
                                           value="${today}" required/>
                                    <button type="submit">Valider</button>
                                </form>
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/backoffice/refuserReservation" method="post">
                                    <input type="hidden" name="idReservation" value="${reservation.idReservation}"/>
                                    <button type="submit">Refuser</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
    
    <p><a href="${pageContext.request.contextPath}/backoffice/dashboard">Retour au tableau de bord</a></p>
</body>
</html>
