FROM openjdk:11-jre-slim
RUN mkdir /opt/app
COPY /target/application-1.0-shaded.jar /opt/app/application-1.0-shaded.jar
CMD ["java", "-jar", "/opt/app/application-1.0-shaded.jar"]