<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Prolongements à valider</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <h1>Prolongements à valider</h1>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <table border="1">
        <tr>
            <th>Adhérent</th>
            <th>Livre</th>
            <th>Date demande</th>
            <th>Date retour prévue après prolongement</th>
            <th>Action</th>
        </tr>
        <c:forEach items="${prolongements}" var="prol">
            <tr>
                <td>${prol.adherent.userAccount.login}</td>
                <td>${prol.pret.exemplaire.livre.titreLivre}</td>
                <td>${prol.dateDemandeProlongement}</td>
                <td>${prol.dateRetourPrevueApresProlongement}</td>
                <td>
                    <form action="${pageContext.request.contextPath}/backoffice/processValidationProlongement" method="post">
                        <input type="hidden" name="idProlongement" value="${prol.idProlongement}"/>
                        <input type="date" name="dateValidation" value="${today}" required/>
                        <input type="hidden" name="action" value="valider"/>
                        <button type="submit">Valider</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/backoffice/processValidationProlongement" method="post">
                        <input type="hidden" name="idProlongement" value="${prol.idProlongement}"/>
                        <input type="hidden" name="action" value="refuser"/>
                        <button type="submit">Refuser</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
    <a href="${pageContext.request.contextPath}/backoffice/dashboard">Retour au dashboard</a>
</body>
</html>
