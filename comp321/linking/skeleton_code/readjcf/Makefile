# Makefile for COMP 321 Project 3: Linking (readjcf)
#
# Uses local csapp library from lib/ directory

# Library location (symlinked in workspace)
LIBDIR  = ../lib

CC      = cc
CFLAGS  = -std=gnu11 -Wall -Wextra -Werror -g -O2 -I${LIBDIR}
LDLIBS  = -lpthread

PROG    = readjcf
OBJS    = readjcf.o csapp.o

all: ${PROG}

${PROG}: ${OBJS}
	${CC} ${CFLAGS} -o ${PROG} ${OBJS} ${LDLIBS}

csapp.o: ${LIBDIR}/csapp.c ${LIBDIR}/csapp.h
	${CC} ${CFLAGS} -c $<

readjcf.o: readjcf.c ${LIBDIR}/csapp.h
	${CC} ${CFLAGS} -c $<

clean:
	${RM} *.o ${PROG} core.[1-9]*

.PHONY: clean all
