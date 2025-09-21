FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY auth-service/pom.xml ./auth-service/
COPY notification-service/pom.xml ./notification-service/
RUN mvn -B dependency:go-offline
COPY notification-service/src ./notification-service/src
RUN mvn -B -pl notification-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S notification_group && adduser -S notification_user -G notification_group
USER notification_user:notification_group
WORKDIR /app
COPY --chown=notification_user:notification_group --from=build /app/notification-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]