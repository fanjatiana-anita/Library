<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Formulaire de prêt</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h1>Formulaire de prêt</h1>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <form action="${pageContext.request.contextPath}/backoffice/pret" method="post">
        <input type="hidden" name="idExemplaire" value="${idExemplaire}">
        <p>
            <label>Livre: </label>
            ${livre.titreLivre} par ${livre.auteur.personne.nomPersonne}
        </p>
        <p>
            <label for="login">Login de l'adhérent: </label>
            <input type="text" id="login" name="login" required>
        </p>
        <p>
            <label for="adjustDirection">Ajustement de la date (si jour non ouvrable): </label>
            <select id="adjustDirection" name="adjustDirection">
                <option value="">Aucun ajustement</option>
                <option value="FORWARD">Avancer au jour ouvrable suivant</option>
                <option value="BACKWARD">Reculer au jour ouvrable précédent</option>
            </select>
        </p>
        <p>
            <input type="submit" value="Enregistrer le prêt">
        </p>
    </form>
    <br>
    <a href="${pageContext.request.contextPath}/backoffice/pret">Retour à la liste des livres</a>
</body>
</html>