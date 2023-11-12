FROM openjdk:8
COPY target/quarkus-app/quarkus-run.jar app.jar
ENTRYPOINT ["java","-jar","/target/quarkus-app/quarkus-run.jar"]