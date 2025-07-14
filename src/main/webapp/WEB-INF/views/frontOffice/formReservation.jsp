<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Formulaire de réservation</title>
    <script>
        function validateDates() {
            const dateReservation = new Date(document.getElementById("dateReservation").value);
            const datePretPrevue = new Date(document.getElementById("datePretPrevue").value);
            if (datePretPrevue < dateReservation) {
                alert("La date du prêt prévu doit être postérieure ou égale à la date de réservation.");
                return false;
            }
            return true;
        }
    </script>
</head>
<body>
    <h1>Réserver un exemplaire</h1>
    <form action="${pageContext.request.contextPath}/frontoffice/processReservation" method="post" onsubmit="return validateDates()">
        <input type="hidden" name="idExemplaire" value="${idExemplaire}"/>
        <label>Date de réservation :</label>
        <input type="date" id="dateReservation" name="dateReservation" value="${dateReservation}" required/><br/>
        <label>Date du prêt prévu :</label>
        <input type="date" id="datePretPrevue" name="datePretPrevue" required/><br/>
        <input type="submit" value="Réserver"/>
    </form>
    <p><a href="${pageContext.request.contextPath}/frontoffice/reservations">Annuler</a></p>
</body>
</html>
