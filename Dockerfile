FROM gradle:7.6.0-jdk17-alpine AS builder
WORKDIR /vitekkor/compoly-bot
COPY . .
RUN gradle clean bootJar

FROM openjdk:17-jdk-slim
WORKDIR /vitekkor/compoly-bot
COPY --from=builder /vitekkor/compoly-bot/compoly-bot/build/libs/compoly-bot-1.0.0-SNAPSHOT.jar .
CMD java -jar compoly-bot-1.0.0-SNAPSHOT.jar --spring.config.location=classpath:/application.yml,optional:/etc/vitekkor/compoly-bot/application.yml
