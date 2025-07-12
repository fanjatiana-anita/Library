<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Inscription d'un adhérent</title>
</head>
<body>
    <h2>Inscription d'un nouvel adhérent</h2>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <c:if test="${not empty success}">
        <p style="color: green;">${success}</p>
    </c:if>
    <form action="${pageContext.request.contextPath}/backoffice/inscription" method="post">
        <h3>Informations personnelles</h3>
        <label>Nom: </label><input type="text" name="nomPersonne" required><br>
        <label>Date de naissance: </label><input type="date" name="dateDeNaissance" required><br>
        <label>Sexe: </label>
        <select name="sexe" required>
            <option value="">Sélectionner</option>
            <option value="M">Masculin</option>
            <option value="F">Féminin</option>
        </select><br>
        <label>Adresse: </label><textarea name="adresse"></textarea><br>

        <h3>Compte utilisateur</h3>
        <label>Login: </label><input type="text" name="login" required><br>
        <label>Mot de passe: </label><input type="password" name="motDePasse" required><br>

        <h3>Adhésion</h3>
        <label>Profil: </label>
        <select name="idProfil" required>
            <option value="">Sélectionner un profil</option>
            <c:forEach items="${profils}" var="profil">
                <option value="${profil.idProfil}">${profil.profil}</option>
            </c:forEach>
        </select><br>
        <label>Statut: </label>
        <select name="statutAdherent" required>
            <option value="ACTIF" selected>Actif</option>
            <option value="SUSPENDU">Suspendu</option>
            <option value="INACTIF">Inactif</option>
        </select><br>
        <label>Date d'adhésion: </label>
        <input type="date" name="dateAdhesion" value="<fmt:formatDate value="${today}" pattern="yyyy-MM-dd"/>" required><br>
        <label>Date de fin d'abonnement (facultatif): </label>
        <input type="date" name="dateFinAbonnement"><br>

        <input type="submit" value="Inscrire">
        <a href="${pageContext.request.contextPath}/backoffice/dashboard">Retour au tableau de bord</a>
    </form>
</body>
</html>