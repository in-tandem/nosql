FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/nosql*.jar
ARG ENV=dev
ENV wiremock_profile=${ENV}
WORKDIR app
COPY ${JAR_FILE} application.jar
COPY src/app.sh app.sh
EXPOSE 9000
RUN chmod 777 app.sh
#ENTRYPOINT ["java","-jar","/application.jar"]
ENTRYPOINT ["/app/app.sh"]