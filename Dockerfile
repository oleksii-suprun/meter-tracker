FROM openjdk:8-jdk AS build

COPY . $HOME/meter-tracker

WORKDIR $HOME/meter-tracker

RUN ./gradlew --no-daemon clean war -Djavacpp.platform=linux-armhf \
    && mv meter-tracker-web/build/libs/meter-tracker-web-1.0-SNAPSHOT.war /meter-tracker.war

FROM arm32v7/tomcat:8-jre8-slim

LABEL maintainer="Aleksey Suprun <aleksey.suprun@gmail.com>"

COPY --from=build /meter-tracker.war $CATALINA_HOME/webapps/meter-tracker.war

EXPOSE 8080

CMD catalina.sh run
