<%@ page contentType="text/html;charset=UTF-8" language="java" %>
     <html>
     <head>
         <title>Accueil - FrontOffice</title>
     </head>
     <body>
         <h2>Bienvenue, ${user.login} !</h2>
         <p>Ceci est la page d'accueil pour les membres.</p>
         <a href="${pageContext.request.contextPath}/logout">Se déconnecter</a>
     </body>
     </html>