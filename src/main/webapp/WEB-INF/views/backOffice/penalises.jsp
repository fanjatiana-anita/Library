<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Adhérents pénalisés</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <h1>Adhérents pénalisés</h1>
    
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    
    <c:choose>
        <c:when test="${empty penalites}">
            <p>Aucun adhérent pénalisé actuellement.</p>
        </c:when>
        <c:otherwise>
            <table border="1">
                <thead>
                    <tr>
                        <th>ID Pénalité</th>
                        <th>Adhérent</th>
                        <th>Date de début</th>
                        <th>Date de fin</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="penalite" items="${penalites}">
                        <tr>
                            <td>${penalite.idPenalisation}</td>
                            <td>${penalite.adherent.userAccount.personne.nomPersonne}</td>
                            <td>
                                <p>${penalite.dateDebutPenalisation}</p>
                                <!-- <fmt:formatDate value="${parsedDateDebut}" pattern="yyyy-MM-dd"/> -->
                            </td>
                            <td>
                                <p>${penalite.dateFinPenalisation}</p>
                                <!-- <fmt:formatDate value="${parsedDateFin}" pattern="yyyy-MM-dd"/> -->
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
