#
#	Makefile for COMP 421 Lab 1 - used by test runner
#
#	Builds montty.o (student device driver) and test programs.
#
MONTTY = montty.o

LANG = cc

TEST = test1 test2 test3 test4 test8 stats invalid-init invalid-stats invalid-read-terminal invalid-write-terminal use-before-init test-large-write
ALL = $(MONTTY) $(TEST)

PUBLIC_DIR = /clear/courses/comp421/lab1

CPPFLAGS = -I$(PUBLIC_DIR)/include -DERROR=-1
CFLAGS = -g -Wall -Wextra -Werror -fno-inline
LDFLAGS = -pthread

all: $(ALL)

$(TEST): $(MONTTY) utils.o $(PUBLIC_DIR)/lib/liblab1.a

utils.o: utils.c utils.h
	$(LANG) $(CFLAGS) $(CPPFLAGS) -c utils.c -o utils.o

clean:
	rm -f $(ALL) utils.o
