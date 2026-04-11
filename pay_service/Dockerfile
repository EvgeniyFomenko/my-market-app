#From tomcat:10.1.48-jdk21
FROM amazoncorretto:21-alpine
COPY ./build/libs/*.jar pay_service.jar
CMD ["java","-jar","/pay_service.jar"]
#COPY ./build/libs/api.jar /usr/local/tomcat/webapps/api.war
#CMD ["catalina.sh","run"]