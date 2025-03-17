FROM openjdk:17

ARG FILE_WAR=target/computerweb-0.0.1-SNAPSHOT.jar

ADD ${FILE_WAR} api-server.jar

ENTRYPOINT ["java"]