# COMP 411: Programming Languages
# Java 8 environment for Jam interpreter assignments

FROM amazoncorretto:8

WORKDIR /workspace

# Install JUnit (vendored for offline builds)
RUN mkdir -p /usr/share/java
COPY lib/junit-platform-console-standalone-1.9.0.jar /usr/share/java/

# Set classpath for JUnit (includes JUnit 4 + Hamcrest inside the standalone jar)
ENV JUNIT_CP=/usr/share/java/junit-platform-console-standalone-1.9.0.jar

# Default: compile and run tests
# Usage: docker run -v ./workspace:/workspace comp411-runner AssignXTest
ENTRYPOINT ["/bin/bash", "-c", "javac -cp .:$JUNIT_CP *.java && java -cp .:$JUNIT_CP org.junit.runner.JUnitCore \"$@\"", "--"]
CMD ["Assign1Test"]
