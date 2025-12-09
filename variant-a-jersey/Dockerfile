FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Download JMX Exporter
ADD https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.19.0/jmx_prometheus_javaagent-0.19.0.jar /app/jmx_prometheus_javaagent.jar
COPY jmx-config.yml /app/jmx-config.yml
EXPOSE 8080 8081
ENTRYPOINT ["java", "-javaagent:/app/jmx_prometheus_javaagent.jar=8081:/app/jmx-config.yml", "-jar", "app.jar"]
