# Use an appropriate base image with OpenJDK and Maven
FROM maven:3.8.4-openjdk-17-slim

# Set the working directory
WORKDIR /app

COPY . /app/

RUN mvn clean install

RUN curl -L -o /app/flyway-commandline-9.21.1-linux-x64.tar.gz https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/9.21.1/flyway-commandline-9.21.1-linux-x64.tar.gz && \
    tar -xzf /app/flyway-commandline-9.21.1-linux-x64.tar.gz -C /app/ && \
    rm /app/flyway-commandline-9.21.1-linux-x64.tar.gz


RUN /app/flyway-9.21.1/flyway migrate -configFiles=/app/flyway.conf

EXPOSE 8080

CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]
