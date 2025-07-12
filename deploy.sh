#!/bin/bash

# Définition des variables
APP_NAME="spring_library_mvc"
SRC_DIR="src/main/java"
WEB_DIR="src/main/webapp"
BUILD_DIR="build"
LIB_DIR="lib"
TOMCAT_WEBAPPS="/home/fanjatiana/apache-tomcat-10.1.28/webapps"

# Construction du classpath avec tous les JAR
CLASSPATH=""
for jar in $LIB_DIR/*.jar; do
  CLASSPATH="$CLASSPATH:$jar"
done

# Nettoyage et création des répertoires temporaires
rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR/WEB-INF/classes
mkdir -p $BUILD_DIR/WEB-INF/lib

# Compilation des fichiers Java
find $SRC_DIR -name "*.java" > sources.txt
javac -parameters -cp "$CLASSPATH" -d $BUILD_DIR/WEB-INF/classes @sources.txt
rm sources.txt

# Copie des fichiers web (JSP, XML, etc.)
cp -r $WEB_DIR/* $BUILD_DIR/

# Copie des dépendances (JAR) dans WEB-INF/lib
cp $LIB_DIR/*.jar $BUILD_DIR/WEB-INF/lib/

# Création du WAR
cd $BUILD_DIR || exit
jar -cvf $APP_NAME.war *
cd ..

# Déploiement
cp -f $BUILD_DIR/$APP_NAME.war $TOMCAT_WEBAPPS/

echo ""
echo "Déploiement terminé. Redémarrez Tomcat si nécessaire."
echo ""
