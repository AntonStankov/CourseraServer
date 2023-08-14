FROM maven:3.8.4-openjdk-17-slim

WORKDIR /app
EXPOSE 8080

RUN curl -L -o /app/flyway-commandline-9.21.1-linux-x64.tar.gz https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/9.21.1/flyway-commandline-9.21.1-linux-x64.tar.gz && \
    tar -xzf /app/flyway-commandline-9.21.1-linux-x64.tar.gz -C /app/ && \
    rm /app/flyway-commandline-9.21.1-linux-x64.tar.gz

COPY ./pom.xml /app/pom.xml
RUN mvn dependency:resolve

COPY . /app/

RUN chmod +x /app/entrypoint.sh
RUN mvn clean install

# CMD ["bash", "/app/entrypoint.sh"]
CMD /app/flyway-9.21.1/flyway migrate -configFiles=/app/flyway.conf & \
    java -jar /app/target/demo-0.0.1-SNAPSHOT.jar