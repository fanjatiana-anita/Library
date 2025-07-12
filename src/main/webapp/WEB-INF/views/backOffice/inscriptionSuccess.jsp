<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Inscription réussie</title>
</head>
<body>
    <h2>Inscription réussie !</h2>
    <p>L’adhérent a été inscrit avec succès.</p>
    <p><strong>Identifiants temporaires :</strong></p>
    <ul>
        <li>Login : ${login}</li>
        <li>Mot de passe : ${motdepasse}</li>
    </ul>
    <p><a href="${pageContext.request.contextPath}/backOffice/dashboard">Retour au tableau de bord</a></p>
</body>
</html>