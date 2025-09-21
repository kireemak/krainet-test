FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY auth-service/pom.xml ./auth-service/
COPY notification-service/pom.xml ./notification-service/
RUN mvn -B dependency:go-offline
COPY auth-service/src ./auth-service/src
RUN mvn -B -pl auth-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S auth_group && adduser -S auth_user -G auth_group
USER auth_user:auth_group
WORKDIR /app
COPY --chown=auth_user:auth_group --from=build /app/auth-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]