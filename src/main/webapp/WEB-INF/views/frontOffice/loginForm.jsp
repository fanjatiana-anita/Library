<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h2>Authentification</h2>
    <form action="${pageContext.request.contextPath}/doLogin" method="post">
        <label for="login">Pseudo:</label>
        <input type="text" id="login" name="login" required>
        <label for="motDePasse">Mot de passe:</label>
        <input type="password" id="motDePasse" name="motDePasse" required>
        <input type="submit" value="S'authentifier">
    </form>
</body>
</html>
