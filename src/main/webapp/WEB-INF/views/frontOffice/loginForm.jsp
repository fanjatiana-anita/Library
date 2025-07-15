<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Connexion</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <h2>Connexion</h2>
    <% if (request.getAttribute("error") != null) { %>
        <p style="color: red;"><%= request.getAttribute("error") %></p>
    <% } %>
    <form action="${pageContext.request.contextPath}/login" method="post">
        
        <label>Login: </label><input type="text" name="login" value="BIB-001"><br>
        <!-- <label>Login: </label><input type="text" name="login" value="MBR-001"><br> -->
        <label>Mot de passe: </label><input type="password" name="password" value="admin456"><br>
        <!-- <label>Mot de passe: </label><input type="password" name="password" value="password123"><br> -->
        <input type="submit" value="Se connecter">
    </form>
</body>
</html>