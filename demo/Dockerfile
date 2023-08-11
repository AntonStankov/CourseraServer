# Use an appropriate base image with OpenJDK and Maven
FROM maven:3.8.4-openjdk-17-slim

# Set the working directory
WORKDIR /app

# Copy your Spring Boot application source code and pom.xml
COPY . /app/

# Build the Spring Boot application using Maven
RUN mvn clean install

# Download and install Flyway CLI manually using curl
RUN curl -L -o /app/flyway-commandline-8.0.2-linux-x64.tar.gz https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/8.0.2/flyway-commandline-8.0.2-linux-x64.tar.gz && \
    tar -xzf /app/flyway-commandline-8.0.2-linux-x64.tar.gz -C /app/ && \
    rm /app/flyway-commandline-8.0.2-linux-x64.tar.gz

# Perform database migration using Flyway (adjust the command based on your setup)
# Example assumes you have a Flyway configuration file named 'flyway.conf'
RUN /app/flyway-8.0.2/flyway migrate -configFiles=/app/flyway.conf

# Expose the necessary port(s)
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]
