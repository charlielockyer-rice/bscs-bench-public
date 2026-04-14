# Makefile for COMP 321 Project 6: Web Proxy
#
# Uses local csapp library from lib/ directory

# Library location (symlinked in workspace)
LIBDIR = lib

CC = cc
CFLAGS = -std=gnu11 -Wall -Wextra -Werror -I${LIBDIR} -g
LDFLAGS = -lpthread

PROG = proxy
OBJS = proxy.o csapp.o

all: proxy

proxy: ${OBJS}
	${CC} ${CFLAGS} -o ${PROG} ${OBJS} ${LDFLAGS}

proxy.o: proxy.c ${LIBDIR}/csapp.h
	${CC} ${CFLAGS} -c proxy.c

csapp.o: ${LIBDIR}/csapp.c ${LIBDIR}/csapp.h
	${CC} ${CFLAGS} -c ${LIBDIR}/csapp.c -o csapp.o

clean:
	${RM} *.o proxy core.[1-9]*

.PHONY: all clean
