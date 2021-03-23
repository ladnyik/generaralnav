rm prodpom.xml
sed  "s#<packaging>jar</packaging>#<packaging>war</packaging>#g" pom.xml > prodpom.xml
mvn vaadin:build-frontend
mvn -f prodpom.xml clean package -Pproduction
