<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Validation Prolongement</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <h1>Valider ou Refuser un Prolongement</h1>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/backoffice/processValidationProlongement">
        <input type="hidden" name="idProlongement" value="${prolongement.idProlongement}" />
        <label>Date de validation :</label>
        <input type="date" name="dateValidation" value="${dateValidation}" required />
        <br/>
        <label>Action :</label>
        <select name="action">
            <option value="valider">Valider</option>
            <option value="refuser">Refuser</option>
        </select>
        <br/>
        <button type="submit">Confirmer</button>
    </form>
    <a href="${pageContext.request.contextPath}/backoffice/prolongements">Retour à la liste</a>
</body>
</html>
