<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Accueil - FrontOffice</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1 { color: #333; }
        .info { margin: 20px 0; }
        table { border-collapse: collapse; width: 100%; max-width: 600px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .links a { margin-right: 10px; text-decoration: none; color: #0066cc; }
        .links a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <h1>Bienvenue, ${login}</h1>
    
    <div class="info">
        <h2>Vos informations</h2>
        <table>
            <tr>
                <th>Information</th>
                <th>Valeur</th>
            </tr>
            <tr>
                <td>Profil</td>
                <td>${adherent.profil.profil}</td>
            </tr>
            <tr>
                <td>Réservations actives (actuel / maximum)</td>
                <td>${nombreReservationsActives} / ${adherent.profil.quotaMaxReservation}</td>
            </tr>
            <tr>
                <td>Prêts en cours (actuel / maximum)</td>
                <td>${nombrePretsActifs} / ${adherent.profil.quotaMaxPret}</td>
            </tr>
            <tr>
                <td>Prolongements utilisés (actuel / maximum)</td>
                <td>${nombreProlongements} / ${adherent.profil.quotaMaxProlongement}</td>
            </tr>
            <tr>
                <td>Statut de pénalisation</td>
                <td>
                    <c:choose>
                        <c:when test="${not empty penalisationActive}">
                            Pénalisé jusqu'au <fmt:formatDate value="${penalisationActive.dateFinPenalisation}" pattern="dd/MM/yyyy"/>
                        </c:when>
                        <c:otherwise>Non pénalisé</c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </table>
    </div>
    
    <div class="links">
        <p><a href="${pageContext.request.contextPath}/frontoffice/reservations">Réserver un livre</a></p>
        <p><a href="${pageContext.request.contextPath}/frontoffice/mesReservations">Voir mes réservations</a></p>
        <p><a href="${pageContext.request.contextPath}/logout">Se déconnecter</a></p>
    </div>
</body>
</html>
