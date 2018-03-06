FROM asuprun/opencv-java:3.4.1

LABEL maintainer="Aleksey Suprun <aleksey.suprun@gmail.com>"

ARG TOMCAT_VERSION=8.5.9

ENV JAVA_HOME "/usr/lib/jvm/java-8-oracle/jre"
ENV CATALINA_HOME "/usr/local/apache-tomcat"

ENV PATH $CATALINA_HOME/bin:$JAVA_HOME/bin:$PATH

RUN mkdir $CATALINA_HOME

RUN set -e \
    && wget "https://archive.apache.org/dist/tomcat/tomcat-8/v$TOMCAT_VERSION/bin/apache-tomcat-8.5.9.tar.gz" \
    && tar -xvf apache-tomcat-$TOMCAT_VERSION.tar.gz -C $CATALINA_HOME --strip-components=1 \
    && rm apache-tomcat-$TOMCAT_VERSION.tar.gz

COPY meter-tracker-web/build/libs/meter-tracker-web-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/meter-tracker.war

EXPOSE 8080

CMD catalina.sh run
