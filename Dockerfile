FROM eclipse-temurin:11-jre
WORKDIR /app
COPY target/*.jar /app/management-app.jar
EXPOSE 8080
CMD ["java", "-jar", "management-app.jar"]