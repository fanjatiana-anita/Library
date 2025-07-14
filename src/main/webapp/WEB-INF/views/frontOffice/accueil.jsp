<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Accueil - FrontOffice</title>
</head>
<body>
    <h1>Bienvenue, ${login}</h1>
    <p><a href="${pageContext.request.contextPath}/frontoffice/reservations">Réserver un livre</a></p>
</body>
</html>