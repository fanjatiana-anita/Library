<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Prêts en cours</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <h1>Prêts en cours</h1>
    
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    
    <c:choose>
        <c:when test="${empty prets}">
            <p>Aucun prêt en cours.</p>
        </c:when>
        <c:otherwise>
            <table border="1">
                <thead>
                    <tr>
                        <th>ID Prêt</th>
                        <th>Adhérent</th>
                        <th>Livre</th>
                        <th>Date de prêt</th>
                        <th>Date de retour prévue</th>
                        <th>Nombre de prolongements</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="pret" items="${prets}">
                        <tr>
                            <td>${pret.idPret}</td>
                            <td>${pret.adherent.userAccount.personne.nomPersonne}</td>
                            <td>${pret.exemplaire.livre.titreLivre}</td>
                            <td>
                                <fmt:parseDate value="${pret.dateDuPret}" pattern="yyyy-MM-dd" var="parsedDateDuPret"/>
                                <fmt:formatDate value="${parsedDateDuPret}" pattern="yyyy-MM-dd"/>
                            </td>
                            <td>
                                <fmt:parseDate value="${pret.dateDeRetourPrevue}" pattern="yyyy-MM-dd" var="parsedDateDeRetourPrevue"/>
                                <fmt:formatDate value="${parsedDateDeRetourPrevue}" pattern="yyyy-MM-dd"/>
                            </td>
                            <td>${pret.nombreProlongement}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
    
    <p><a href="${pageContext.request.contextPath}/backoffice/dashboard">Retour au tableau de bord</a></p>
</body>
</html>
