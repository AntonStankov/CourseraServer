FROM maven:3.8.4-openjdk-17-slim
WORKDIR /app
COPY . /app/
RUN chmod +x /app/entrypoint.sh
RUN mvn clean install
RUN curl -L -o /app/flyway-commandline-9.21.1-linux-x64.tar.gz https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/9.21.1/flyway-commandline-9.21.1-linux-x64.tar.gz && \
    tar -xzf /app/flyway-commandline-9.21.1-linux-x64.tar.gz -C /app/ && \
    rm /app/flyway-commandline-9.21.1-linux-x64.tar.gz
EXPOSE 8080
CMD ["/app/entrypoint.sh"]
