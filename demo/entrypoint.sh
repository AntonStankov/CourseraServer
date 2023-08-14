#!/bin/bash
/app/flyway-9.21.1/flyway migrate -configFiles=/app/flyway.conf &
java -jar /app/target/demo-0.0.1-SNAPSHOT.jar
