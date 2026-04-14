#
#	Sample Makefile for COMP 421 Yalnix kernel and user programs.
#
#	The Yalnix kernel built will be named "yalnix".  *ALL* kernel
#	Makefiles for this lab must have a "yalnix" rule in them, and
#	must produce a kernel executable named "yalnix" -- we will run
#	your Makefile and will grade the resulting executable
#	named "yalnix".
#

#
#	Define the list of user test programs to be made.
#
ALL = yalnix

#
#	You must modify the KERNEL_OBJS and KERNEL_SRCS definitions.
#
KERNEL_OBJS = yalnix.o
KERNEL_SRCS = yalnix.c

PUBLIC_DIR = /clear/courses/comp421/pub

CPPFLAGS = -I$(PUBLIC_DIR)/include
CFLAGS = -g -Wall -Wextra -Werror

LANG = gcc

%: %.o
	$(LINK.o) -o $@ $^ $(LOADLIBES) $(LDLIBS)

LINK.o = $(PUBLIC_DIR)/bin/link-user-$(LANG) $(LDFLAGS) $(TARGET_ARCH)

%: %.c
%: %.cc
%: %.cpp

all: $(ALL)

yalnix: $(KERNEL_OBJS)
	$(PUBLIC_DIR)/bin/link-kernel-$(LANG) -o yalnix $(KERNEL_OBJS)

clean:
	rm -f $(KERNEL_OBJS) $(ALL)

depend:
	$(CC) $(CPPFLAGS) -M $(KERNEL_SRCS) > .depend

#include .depend
