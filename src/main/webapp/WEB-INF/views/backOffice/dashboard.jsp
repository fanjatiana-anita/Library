<%@ page contentType="text/html;charset=UTF-8" language="java" %>
     <html>
     <head>
         <title>Tableau de bord - BackOffice</title>
     </head>
     <body>
         <h2>Bienvenue, ${user.login} !</h2>
         <p>Ceci est le tableau de bord pour les bibliothécaires.</p>
         <a href="${pageContext.request.contextPath}/logout">Se déconnecter</a>
     </body>
     </html>