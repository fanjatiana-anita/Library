<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Tableau de bord - Bibliothèque</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <h1>Tableau de bord</h1>
    <c:if test="${not empty message}">
        <p style="color: green;">${message}</p>
    </c:if>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <ul>
        <li><a href="${pageContext.request.contextPath}/backoffice/inscription">Inscription</a></li>
        <li><a href="${pageContext.request.contextPath}/backoffice/reabonnement">Réabonnement</a></li>
        <li><a href="${pageContext.request.contextPath}/backoffice/pret">Gestion des prêts</a></li>
        <li><a href="${pageContext.request.contextPath}/backoffice/return">Retour des prêts</a></li>
        <li><a href="${pageContext.request.contextPath}/backoffice/reservationsEnAttente">Valider/Refuser les demandes de réservation</a></li>
        <li><a href="${pageContext.request.contextPath}/backoffice/reservationsValidees">Voir les réservations validées</a></li>
        <li><a href="${pageContext.request.contextPath}/backoffice/penalises">Adhérents pénalisés</a></li>
        <li><a href="${pageContext.request.contextPath}/backoffice/historique">Historique des paiements et états</a></li>
        <li><a href="${pageContext.request.contextPath}/backoffice/pretsEnCours">Prêts en cours</a></li>
        <p><a href="${pageContext.request.contextPath}/backoffice/parametres">Paramètres du système</a></p>
        <li><a href="${pageContext.request.contextPath}/logout">Se déconnecter</a></li>
    </ul>
</body>
</html>
