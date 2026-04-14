# COMP 310: Advanced Object-Oriented Programming
# Java 17 environment with pre-compiled provided libraries and JUnit 5
# All dependencies pre-built for offline operation

FROM eclipse-temurin:17-jdk-jammy

# ── JUnit 5 dependencies ─────────────────────────────────────────────
# The standalone JAR bundles api + engine, so only one download needed
RUN mkdir -p /opt/comp310/junit5 && \
    cd /opt/comp310/junit5 && \
    curl -fsSL -O https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar

# ── Compile provided library sources into provided.jar ────────────────
COPY lib/ /opt/comp310/lib-src/
RUN mkdir -p /opt/comp310/provided-classes && \
    find /opt/comp310/lib-src/provided/utils \
         /opt/comp310/lib-src/provided/logger \
         /opt/comp310/lib-src/provided/listFW \
         /opt/comp310/lib-src/provided/basicVisitorFW \
         /opt/comp310/lib-src/provided/extvisitor \
         /opt/comp310/lib-src/provided/ballworld \
         -name "*.java" ! -name "module-info.java" > /tmp/sources.txt && \
    javac -d /opt/comp310/provided-classes @/tmp/sources.txt && \
    jar cf /opt/comp310/provided.jar -C /opt/comp310/provided-classes . && \
    rm -rf /opt/comp310/provided-classes /opt/comp310/lib-src /tmp/sources.txt

# ── Copy test suites and build script ─────────────────────────────────
COPY tests/ /opt/comp310/tests/
COPY build.sh /opt/comp310/build.sh
RUN chmod +x /opt/comp310/build.sh

WORKDIR /workspace

CMD ["/opt/comp310/build.sh"]
