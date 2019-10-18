FROM openjdk:12
WORKDIR /app
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n","-Djava.security.egd=file:/dev/./urandom","-jar","target/ocsen-0.0.1.jar"]