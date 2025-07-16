<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Retour d'un prêt</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h1>Retour d'un prêt</h1>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <form action="${pageContext.request.contextPath}/backoffice/return" method="post">
        <p>
            <label for="login">Login de l'adhérent: </label>
            <input type="text" id="login" name="login" required>
        </p>
        <p>
            <input type="submit" value="Afficher les prêts en cours">
        </p>
    </form>
    <c:if test="${not empty prets}">
        <h2>Prêts en cours pour l'adhérent</h2>
        <table border="1">
            <tr>
                <th>ID Prêt</th>
                <th>Livre</th>
                <th>Exemplaire ID</th>
                <th>Date du prêt</th>
                <th>Date de retour prévue</th>
                <th>Action</th>
            </tr>
            <c:forEach var="pretMap" items="${prets}">
                <tr>
                    <td>${pretMap.pret.idPret}</td>
                    <td>${pretMap.pret.exemplaire.livre.titreLivre}</td>
                    <td>${pretMap.pret.exemplaire.idExemplaire}</td>
                    <td>
                        <p>${pretMap.dateDuPret}</p>
                    </td>
                    <td>
                        <p>${pretMap.dateDeRetourPrevue}</p>
                    </td>
                    <td><a href="${pageContext.request.contextPath}/backoffice/formReturn?idPret=${pretMap.pret.idPret}">Retourner</a></td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
    <br>
    <a href="${pageContext.request.contextPath}/backoffice/dashboard">Retour au tableau de bord</a>
</body>
</html>