FROM amazoncorretto:11-alpine-jdk
MAINTAINER ship-service
COPY target/ship_service.jar ship_service.jar
ENTRYPOINT ["java","-jar","/ship_service.jar"]