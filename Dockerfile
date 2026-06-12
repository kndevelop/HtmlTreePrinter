FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle build

FROM mcr.microsoft.com/playwright/java:v1.54.0
WORKDIR /app
COPY --from=builder /app/build/libs/HtmlTreePrinter.jar /app/app.jar
RUN mkdir -p /app/output

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
CMD ["default_site"]
