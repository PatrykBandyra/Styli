FROM gradle:7.5.1-jdk17-alpine AS build

ENV SPRING_PROFILES_ACTIVE="prod"

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle assemble

FROM openjdk:17-jdk-slim
EXPOSE 8000
COPY --from=build /home/gradle/src/build/libs/styli-api-0.0.1-SNAPSHOT.jar /app/
RUN bash -c 'touch /app/styli-api-0.0.1-SNAPSHOT.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=prod", "-jar", "/app/styli-api-0.0.1-SNAPSHOT.jar"]