# Makefile for COMP 321 Project 2: Word Count

CC      = cc
CFLAGS  = -std=gnu11 -Wall -Wextra -Werror -g -O2

PROG    = count
OBJS    = count.o

all: ${PROG}

${PROG}: ${OBJS}
	${CC} ${CFLAGS} -o ${PROG} ${OBJS}

count.o: count.c
	${CC} ${CFLAGS} -c $<

clean:
	${RM} *.o ${PROG} core.[1-9]*

.PHONY: clean all
