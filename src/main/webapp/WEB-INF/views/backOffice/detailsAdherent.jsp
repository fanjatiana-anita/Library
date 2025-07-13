<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Détails de l'adhérent</title>
</head>
<body>
    <h2>Détails de l'adhérent</h2>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <h3>Informations personnelles</h3>
    <p><strong>Nom :</strong> ${personne.nomPersonne}</p>
    <p><strong>Date de naissance :</strong> ${personne.dateDeNaissance}</p>
    <p><strong>Sexe :</strong> ${personne.sexe}</p>
    <p><strong>Adresse :</strong> ${personne.adresse}</p>

    <h3>Compte utilisateur</h3>
    <p><strong>Login :</strong> ${userAccount.login}</p>
    <p><strong>Rôle :</strong> ${userAccount.role}</p>

    <h3>Adhésion</h3>
    <p><strong>Profil :</strong> ${adherent.profil.profil}</p>
    <p><strong>Statut :</strong> ${adherent.statutAdherent}</p>
    <p><strong>Date d'adhésion :</strong> ${adherent.dateAdhesion}</p>

    <h3>Abonnement</h3>
    <p><strong>Date de début :</strong> ${abonnement.dateDebut}</p>
    <p><strong>Date de fin :</strong> ${abonnement.dateFin}</p>

    <a href="${pageContext.request.contextPath}/backoffice/reabonnement">Retour au réabonnement</a>
    <a href="${pageContext.request.contextPath}/backoffice/dashboard">Retour au tableau de bord</a>
</body>
</html>