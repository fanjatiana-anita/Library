<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Changer le mot de passe</title>
</head>
<body>
    <h2>Changer votre mot de passe</h2>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <c:if test="${not empty message}">
        <p style="color: green;">${message}</p>
    </c:if>
    <form action="${pageContext.request.contextPath}/changerMotDePasse" method="post">
        <label>Mot de passe actuel:</label>
        <input type="password" name="motDePasseActuel" required><br>

        <label>Nouveau mot de passe:</label>
        <input type="password" name="nouveauMotDePasse" required><br>

        <label>Confirmer le nouveau mot de passe:</label>
        <input type="password" name="confirmationMotDePasse" required><br>

        <input type="submit" value="Changer le mot de passe">
    </form>
</body>
</html>