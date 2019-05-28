FROM java:8-jdk-alpine
FROM tomcat:9.0.16-jre8
LABEL maintainer="faridghafooriadl@gmail.com"
CMD mvn clean package install
COPY ./target/IE_CA8_backend.war /back/app
WORKDIR ./back/app
RUN sh -c 'touch IE_CA8_backend.jar'
ENTRYPOINT ["java","-jar","/IE_CA8_backend.jar"]