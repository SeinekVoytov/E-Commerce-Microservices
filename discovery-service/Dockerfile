FROM openjdk:21 as build

COPY target/*.jar app.jar

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf /app.jar)

FROM openjdk:21

VOLUME /tmp

ARG DEPENDENCY=/target/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

HEALTHCHECK --interval=30s --timeout=30s CMD curl -f http://localhost:8761/actuator/health || exit 1

EXPOSE 8761

ENTRYPOINT ["java","-cp","app:app/lib/*","org/example/discoveryservice/EurekaServerApplication"]