FROM openjdk:8-jdk-stretch
WORKDIR /
ADD wait-for.sh /
RUN apt-get update && apt-get -y install netcat && apt-get clean
ADD controller-all-1.0.jar controller-all-1.0.jar
CMD ["./wait-for.sh", "rabbitmq:5672", "--", "java", "-jar", "controller-all-1.0.jar"]

