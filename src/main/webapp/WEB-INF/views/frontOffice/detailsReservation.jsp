<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Détails de la réservation</title>
</head>
<body>
    <h1>Réservation enregistrée</h1>
    <p>${message}</p>
    <p>ID Réservation : ${reservation.idReservation}</p>
    <p>Livre : ${reservation.exemplaire.livre.titreLivre}</p>
    <p>Exemplaire : ${reservation.exemplaire.idExemplaire}</p>
    <p>Date de réservation : ${reservation.dateDeReservation}</p>
    <p>Date du prêt prévu : ${reservation.dateDuPretPrevue}</p>
    <p>Statut : ${reservation.statutReservation}</p>
    <p><a href="${pageContext.request.contextPath}/frontoffice">Retour à l'accueil</a></p>
</body>
</html>
