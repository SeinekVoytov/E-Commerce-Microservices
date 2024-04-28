FROM openjdk:21 as build

COPY target/*.jar app.jar

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf /app.jar)

FROM openjdk:21

VOLUME /tmp

ARG DEPENDENCY=/target/dependency
ARG CONFIG_DIR=/app/data

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

RUN mkdir ${CONFIG_DIR}

HEALTHCHECK --interval=30s --timeout=30s CMD curl -f http://localhost:8888/actuator/health || exit 1

EXPOSE 8888

ENTRYPOINT ["java","-cp","app:app/lib/*","org/example/configservice/ConfigServiceApplication"]