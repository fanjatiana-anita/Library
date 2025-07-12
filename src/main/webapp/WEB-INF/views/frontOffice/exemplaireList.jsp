<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Exemplaires disponibles pour ${livre.titreLivre}</title>
</head>
<body>
    <h2>Exemplaires disponibles pour ${livre.titreLivre}</h2>
    <c:if test="${empty exemplaires}">
        <p>Aucun exemplaire disponible pour ce livre.</p>
    </c:if>
    <c:if test="${not empty exemplaires}">
        <form action="${pageContext.request.contextPath}/frontoffice/reserver" method="post">
            <label>Sélectionner un exemplaire :</label>
            <select name="idExemplaire" required>
                <c:forEach var="exemplaire" items="${exemplaires}">
                    <option value="${exemplaire.idExemplaire}">Exemplaire ${exemplaire.idExemplaire}</option>
                </c:forEach>
            </select><br>
            <label>Date de prêt prévue :</label>
            <input type="date" name="datePretPrevue" required><br>
            <input type="submit" value="Réserver">
        </form>
    </c:if>
    <p><a href="${pageContext.request.contextPath}/frontoffice/reservations">Retour à la liste des livres</a></p>
</body>
</html>