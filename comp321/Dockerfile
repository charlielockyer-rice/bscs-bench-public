# COMP 321: Introduction to Computer Systems
# GCC 13 environment for C systems programming assignments
# Includes csapp library, make, gdb, valgrind

FROM gcc:13

# Install development tools
RUN apt-get update && apt-get install -y --no-install-recommends \
    make \
    gdb \
    valgrind \
    && rm -rf /var/lib/apt/lists/*

# Install csapp library to /opt/csapp
COPY lib/csapp.h lib/csapp.c /opt/csapp/

# Pre-compile csapp.o for faster builds
RUN cd /opt/csapp && \
    gcc -std=gnu11 -c -Wall -Wextra -O2 -o csapp.o csapp.c

# Set include path for csapp.h
ENV CPATH=/opt/csapp
ENV LIBRARY_PATH=/opt/csapp

# Working directory for workspace mounts
WORKDIR /workspace

# Default command (overridden by runner)
CMD ["make"]
