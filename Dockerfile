# FOR BUILD DOCEKR
# docker build --tag spring .

# FOR RUN DOCKER
# docker run -d --name backend spring

FROM java:8-jdk-alpine
FROM tomcat:9.0.16-jre8
VOLUME /backend
LABEL maintainer="faridghafooriadl@gmail.com"
CMD mvn clean package install
COPY ./target/IE_CA8_backend.war /usr/local/tomcat/webapps/app.war
EXPOSE 8080
RUN sh -c "touch /usr/local/tomcat/webapps/app.war"
CMD ["catalina.sh", "run"]