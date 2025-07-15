<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Mes Prêts</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <h1>Mes Prêts en Cours</h1>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <c:if test="${not empty message}">
        <p style="color: green;">${message}</p>
    </c:if>
    <c:choose>
        <c:when test="${empty prets}">
            <p>Aucun prêt en cours.</p>
        </c:when>
        <c:otherwise>
            <table border="1">
                <tr>
                    <th>Titre du Livre</th>
                    <th>Date du Prêt</th>
                    <th>Date de Retour Prévue</th>
                    <th>Action</th>
                </tr>
                <c:forEach items="${prets}" var="pretDisplay">
                    <tr>
                        <td>${pretDisplay.pret.exemplaire.livre.titreLivre}</td>
                        <td><fmt:formatDate value="${pretDisplay.dateDuPretAsDate}" pattern="yyyy-MM-dd"/></td>
                        <td><fmt:formatDate value="${pretDisplay.dateDeRetourPrevueAsDate}" pattern="yyyy-MM-dd"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${pretDisplay.prolongement == null && pretDisplay.quotaProlongement > 0}">
                                    <a href="${pageContext.request.contextPath}/frontoffice/formProlongement?idPret=${pretDisplay.pret.idPret}">Demander un prolongement</a>
                                    <br/>
                                </c:when>
                                <c:otherwise>
                                    Statut : ${pretDisplay.statutProlongement}
                                    <c:if test="${pretDisplay.prolongement != null}">
                                        <a href="${pageContext.request.contextPath}/frontoffice/detailsProlongement?idProlongement=${pretDisplay.prolongement.idProlongement}">Voir le détail</a>
                                    </c:if>
                                    <br/>
    
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>
    <a href="${pageContext.request.contextPath}/frontoffice/accueil">Retour à l'accueil</a>
</body>
</html>