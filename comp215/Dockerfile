# COMP 215: Program Design
# Java 17 + JUnit 5 environment for Java programming assignments
# Uses direct javac compilation (no Maven)

FROM eclipse-temurin:17-jdk

WORKDIR /workspace

# Install Python3 (needed for Homework 5: Tester Framework)
RUN apt-get update && apt-get install -y python3 && rm -rf /var/lib/apt/lists/*

# Create JUnit directory
RUN mkdir -p /opt/junit

# Copy JUnit and JSON JARs
COPY lib/junit-platform-console-standalone-1.9.0.jar /opt/junit/
COPY lib/json-20210307.jar /opt/junit/

# Set classpath environment variable for JARs
ENV JUNIT_CLASSPATH="/opt/junit/junit-platform-console-standalone-1.9.0.jar:/opt/junit/json-20210307.jar"

# Verify JARs are accessible
RUN ls -la /opt/junit/

CMD ["bash"]
