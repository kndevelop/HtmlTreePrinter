FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . /app
RUN gradle build

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/HtmlTreePrinter.jar /app/app.jar
RUN mkdir -p /app/output

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
CMD ["default_site"]
