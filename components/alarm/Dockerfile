FROM openjdk:8-jdk-stretch
WORKDIR /
ADD wait-for.sh /
RUN apt-get update && apt-get -y install netcat && apt-get clean
ADD alarm-all-1.0.jar alarm-all-1.0.jar

