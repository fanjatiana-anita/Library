<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Enregistrer un Prêt</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">

</head>
<body>
    <h1>Enregistrer un Prêt</h1>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <form action="${pageContext.request.contextPath}/backoffice/pret" method="post">
        <input type="hidden" name="idExemplaire" value="${idExemplaire}"/>
        <input type="hidden" name="idReservation" value="${idReservation}"/>
        <p>
            <label>Livre :</label>
            <input type="text" value="${livre.titreLivre}" readonly/>
        </p>
        <p>
            <label>Adhérent (login) :</label>
            <c:choose>
                <c:when test="${not empty login}">
                    <input type="text" name="login" value="${login}" readonly/>
                </c:when>
                <c:otherwise>
                    <input type="text" name="login" required/>
                </c:otherwise>
            </c:choose>
        </p>
        <p>
            <label>Date du prêt :</label>
            <input type="date" name="datePret" value="${today}" required/>
        </p>
        <p>
            <label>Date de retour prévue (optionnel) :</label>
            <input type="date" name="dateDeRetourPrevue"/>
        </p>
        <p>
            <label>Ajustement des jours non ouvrables :</label>
            <select name="adjustDirection">
                <option value="FORWARD" selected>Avancer</option>
                <option value="BACKWARD">Reculer</option>
            </select>
        </p>
        <input type="submit" value="Enregistrer le Prêt"/>
    </form>
    <p><a href="${pageContext.request.contextPath}/backoffice">Retour</a></p>
</body>
</html>