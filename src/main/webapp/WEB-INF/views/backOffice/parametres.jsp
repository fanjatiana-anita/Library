<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Paramètres du système</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">

    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1 { color: #333; }
        table { border-collapse: collapse; width: 100%; max-width: 600px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .links a { margin-right: 10px; text-decoration: none; color: #0066cc; }
        .links a:hover { text-decoration: underline; }
        .error { color: red; }
        .success { color: green; }
    </style>
</head>
<body>
    <h1>Paramètres du système</h1>
    
    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>
    <c:if test="${not empty message}">
        <p class="success">${message}</p>
    </c:if>

    <h2>Modifier les délais supplémentaires de réservation</h2>
    <table>
        <thead>
            <tr>
                <th>Profil</th>
                <th>Délai supplémentaire (jours)</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="profil" items="${profils}">
                <tr>
                    <td>${profil.profil}</td>
                    <td>
                        <form action="${pageContext.request.contextPath}/backoffice/updateDelaiReservation" method="post">
                            <input type="hidden" name="idProfil" value="${profil.idProfil}"/>
                            <input type="number" name="delaiSupplementaireReservation" 
                                   value="${profil.delaiSupplementaireReservation}" min="0" required/>
                            <button type="submit">Mettre à jour</button>
                        </form>
                    </td>
                    <td>
                        <c:if test="${profil.delaiSupplementaireReservation != 2}">
                            <a href="${pageContext.request.contextPath}/backoffice/resetDelaiReservation?idProfil=${profil.idProfil}">
                                Réinitialiser à 2 jours
                            </a>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <div class="links">
        <p><a href="${pageContext.request.contextPath}/backoffice/dashboard">Retour au tableau de bord</a></p>
        <p><a href="${pageContext.request.contextPath}/logout">Se déconnecter</a></p>
    </div>
</body>
</html>
