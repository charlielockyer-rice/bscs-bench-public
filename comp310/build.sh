#!/bin/bash
# COMP 310 Build & Test Script
# Compiles student code against provided.jar, compiles tests, runs JUnit 5.
#
# Usage: build.sh <hwNN> [workspace_path]
#   $1 - assignment name (hw01, hw02, ..., hw06)
#   $2 - workspace mount path (default: /workspace)

set -euo pipefail

HW=${1:?Usage: build.sh <hwNN> [workspace_path]}
WORKSPACE=${2:-/workspace}
BUILD_DIR=/tmp/build
REPORTS_DIR=$WORKSPACE/test-reports

# Classpath components
PROVIDED_JAR=/opt/comp310/provided.jar
JUNIT_DIR=/opt/comp310/junit5
JUNIT_STANDALONE=$JUNIT_DIR/junit-platform-console-standalone-1.10.2.jar

# ── Clean ─────────────────────────────────────────────────────────────
rm -rf "$BUILD_DIR" "$REPORTS_DIR"
mkdir -p "$BUILD_DIR" "$REPORTS_DIR"

# ── 1. Compile student code against provided.jar ─────────────────────
STUDENT_SRCS=$(find "$WORKSPACE/src" -name "*.java" ! -name "module-info.java" 2>/dev/null || true)
if [ -z "$STUDENT_SRCS" ]; then
    echo "ERROR: No Java source files found in $WORKSPACE/src" >&2
    exit 1
fi

echo "==> Compiling student source files..."
javac -cp "$PROVIDED_JAR" \
      -d "$BUILD_DIR" \
      $STUDENT_SRCS 2>&1

echo "==> Student code compiled successfully."

# ── 2. Compile test sources against student code + provided + JUnit ──
TEST_ROOT=${COMP310_TESTS_DIR:-/opt/comp310/tests}
TEST_DIR=$TEST_ROOT/$HW
if [ -d "$TEST_DIR" ]; then
    TEST_SRCS=$(find "$TEST_DIR" -name "*.java" 2>/dev/null || true)
    if [ -n "$TEST_SRCS" ]; then
        echo "==> Compiling test files from $TEST_DIR..."
        javac -cp "$PROVIDED_JAR:$BUILD_DIR:$JUNIT_DIR/*" \
              -d "$BUILD_DIR" \
              $TEST_SRCS 2>&1
        echo "==> Tests compiled successfully."
    else
        echo "WARNING: No test source files found in $TEST_DIR" >&2
    fi
else
    echo "WARNING: Test directory $TEST_DIR does not exist" >&2
fi

# ── 3. Run JUnit 5 tests ─────────────────────────────────────────────
echo "==> Running JUnit 5 tests..."
set +e
java -jar "$JUNIT_STANDALONE" \
     --class-path "$BUILD_DIR:$PROVIDED_JAR" \
     --scan-classpath \
     --reports-dir "$REPORTS_DIR" \
     --fail-if-no-tests \
     2>&1
TEST_EXIT=$?
set -e

# ── 4. Optional fat JAR (skipped by default, set BUILD_JAR=1 to enable)
if [ "${BUILD_JAR:-0}" = "1" ]; then
    FAT_JAR=$WORKSPACE/submission.jar
    echo "==> Building submission JAR..."
    mkdir -p /tmp/fatjar
    cp -r "$BUILD_DIR"/* /tmp/fatjar/ 2>/dev/null || true
    cd /tmp/fatjar
    jar xf "$PROVIDED_JAR" 2>/dev/null || true
    jar cf "$FAT_JAR" -C /tmp/fatjar . 2>/dev/null && \
        echo "==> Submission JAR created: $FAT_JAR" || \
        echo "WARNING: Failed to create submission JAR (non-fatal)" >&2
    rm -rf /tmp/fatjar
fi

exit $TEST_EXIT
