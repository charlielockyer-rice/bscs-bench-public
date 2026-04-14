# COMP 322: Fundamentals of Parallel Programming
# Java 11 + Maven environment for HJlib-based assignments
# All dependencies pre-cached for offline operation

FROM maven:3.9.6-eclipse-temurin-11

WORKDIR /workspace

# Install HJlib to Maven local repo
COPY lib/hjlib.jar /tmp/hjlib.jar
RUN mvn install:install-file \
    -Dfile=/tmp/hjlib.jar \
    -DgroupId=com.github.RiceParProgCourse \
    -DartifactId=hjlib-elog-aedancullen \
    -Dversion=master-d2108184ec-1 \
    -Dpackaging=jar \
    -q && rm /tmp/hjlib.jar

# Copy real project and run FULL build cycle to cache ALL plugins and dependencies
COPY hw1-charlielockyer-rice /tmp/hw1
RUN cd /tmp/hw1 && \
    mvn clean test -q || true && \
    mvn dependency:go-offline -q || true && \
    rm -rf /tmp/hw1

CMD ["mvn", "clean", "test"]
