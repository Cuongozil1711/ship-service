FROM amazoncorretto:11-alpine-jdk
MAINTAINER manager-service
COPY target/manager_service.jar manager_service.jar
ENTRYPOINT ["java","-jar","/manager_service.jar"]