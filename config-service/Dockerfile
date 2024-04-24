FROM openjdk:21 as build

COPY target/*.jar app.jar

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf /app.jar)

FROM openjdk:21

VOLUME /tmp

ARG DEPENDENCY=/target/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENV CONFIG_DIR="/Users/sashavoytov/Downloads/configuration"
ENV GITHUB_CONFIG_REPO_URI="https://github.com/SeinekVoytov/E-Commerce-Microservices-Properties"

ENTRYPOINT ["java","-cp","app:app/lib/*","org/example/configservice/ConfigServiceApplication"]