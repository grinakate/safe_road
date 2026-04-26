FROM maven:3.9.12-eclipse-temurin-25 AS build

COPY . /build
WORKDIR /build

RUN mvn clean package -DskipTests=true -B

FROM eclipse-temurin:25-jre-alpine AS runtime

COPY --from=build /build/app-main/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
