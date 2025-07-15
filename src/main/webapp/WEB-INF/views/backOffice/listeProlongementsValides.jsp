<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Prolongements validés</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <h1>Prolongements validés</h1>
    <table border="1">
        <tr>
            <th>Adhérent</th>
            <th>Livre</th>
            <th>Date demande</th>
            <th>Date retour prévue après prolongement</th>
            <!-- <th>Date validation</th> -->
        </tr>
        <c:forEach items="${prolongements}" var="prol">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${not empty prol.adherent and not empty prol.adherent.userAccount}">
                            ${prol.adherent.userAccount.login}
                        </c:when>
                        <c:otherwise>?</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${not empty prol.pret and not empty prol.pret.exemplaire and not empty prol.pret.exemplaire.livre}">
                            ${prol.pret.exemplaire.livre.titreLivre}
                        </c:when>
                        <c:otherwise>?</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${not empty prol.dateDemandeProlongement}">
                            ${prol.dateDemandeProlongement}
                        </c:when>
                        <c:otherwise>?</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${not empty prol.dateRetourPrevueApresProlongement}">
                            ${prol.dateRetourPrevueApresProlongement}
                        </c:when>
                        <c:otherwise>?</c:otherwise>
                    </c:choose>
                </td>
                <!-- <td>${prol.dateValidation}</td> -->
            </tr>
        </c:forEach>
    </table>
    <a href="${pageContext.request.contextPath}/backoffice/dashboard">Retour au dashboard</a>
</body>
</html>
