#FROM ghcr.io/graalvm/jdk:java17-22.3.1
FROM openjdk:17
#FROM gcr.io/distroless/base-debian11
EXPOSE 3004

ARG PORT=3004
ENV CONFIG_PATH=/config
ENV DB_PATH=/db
ENV PORT=${PORT}
ENV DEBUG=1

VOLUME ["/config", "/db"]

COPY ./target/heater-1.0-SNAPSHOT-jar-with-dependencies.jar /tmp
WORKDIR /tmp

CMD java -jar heater-1.0-SNAPSHOT-jar-with-dependencies.jar $PORT

