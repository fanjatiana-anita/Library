<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Historique des paiements et états</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <h1>Historique des paiements et états</h1>
    
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    
    <h2>Historique des paiements</h2>
    <c:choose>
        <c:when test="${empty paiements}">
            <p>Aucun paiement enregistré.</p>
        </c:when>
        <c:otherwise>
            <table border="1">
                <thead>
                    <tr>
                        <th>ID Paiement</th>
                        <th>Adhérent</th>
                        <th>Date de paiement</th>
                        <th>Montant</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="paiement" items="${paiements}">
                        <tr>
                            <td>${paiement.idPaiement}</td>
                            <td>${paiement.adherent.userAccount.personne.nomPersonne}</td>
                            <td>
                                <fmt:parseDate value="${paiement.datePaiement}" pattern="yyyy-MM-dd" var="parsedDatePaiement"/>
                                <fmt:formatDate value="${parsedDatePaiement}" pattern="yyyy-MM-dd"/>
                            </td>
                            <td>${paiement.montantCotisation}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
    
    <h2>Historique des états</h2>
    <c:choose>
        <c:when test="${empty etats}">
            <p>Aucun changement d'état enregistré.</p>
        </c:when>
        <c:otherwise>
            <table border="1">
                <thead>
                    <tr>
                        <th>ID Historique</th>
                        <th>Entité</th>
                        <th>ID Entité</th>
                        <th>État avant</th>
                        <th>État après</th>
                        <th>Date de changement</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="etat" items="${etats}">
                        <tr>
                            <td>${etat.idHistorique}</td>
                            <td>${etat.entite}</td>
                            <td>${etat.idEntite}</td>
                            <td>${etat.etatAvant != null ? etat.etatAvant : 'N/A'}</td>
                            <td>${etat.etatApres}</td>
                            <td>
                                <fmt:parseDate value="${etat.dateChangement}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDateChangement"/>
                                <fmt:formatDate value="${parsedDateChangement}" pattern="yyyy-MM-dd HH:mm:ss"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
    
    <p><a href="${pageContext.request.contextPath}/backoffice/dashboard">Retour au tableau de bord</a></p>
</body>
</html>
