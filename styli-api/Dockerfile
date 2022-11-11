FROM gradle:7.5.1-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM openjdk:17-jre-slim
EXPOSE 8000
COPY --from=build /home/gradle/src/build/libs/styli-api-0.0.1-SNAPSHOT.jar /app/
RUN bash -c 'touch /app/styli-api-0.0.1-SNAPSHOT.jar'
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/styli-api-0.0.1-SNAPSHOT.jar"]