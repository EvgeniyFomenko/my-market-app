#From tomcat:10.1.48-jdk21
FROM amazoncorretto:21-alpine
COPY ./build/libs/*.jar api.jar
CMD ["java","-jar","/api.jar"]
#COPY ./build/libs/api.jar /usr/local/tomcat/webapps/api.war
#CMD ["catalina.sh","run"]