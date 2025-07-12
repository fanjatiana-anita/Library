<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Inscription d'un membre</title>
</head>
<body>
    <h2>Inscrire un nouvel adhérent</h2>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <form action="${pageContext.request.contextPath}/inscription" method="post">
        <label>Nom:</label>
        <input type="text" name="nom" required><br>

        <label>Date de naissance:</label>
        <input type="date" name="dateNaissance" required><br>

        <label>Sexe:</label>
        <select name="sexe" required>
            <option value="Homme">Homme</option>
            <option value="Femme">Femme</option>
        </select><br>

        <label>Adresse:</label>
        <input type="text" name="adresse" required><br>

        <label>Login:</label>
        <input type="text" name="login" required><br>

        <label>Profil:</label>
        <select name="profilId" required>
            <c:forEach var="profil" items="${profils}">
                <option value="${profil.idProfil}">${profil.profil}</option>
            </c:forEach>
        </select><br>

        <input type="submit" value="Inscrire">
    </form>
</body>
</html>