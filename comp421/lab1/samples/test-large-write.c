#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <threads.h>
#include <terminals.h>
#include <unistd.h>

#include "utils.h"

/*
 * Test writing a buffer larger than TERMINAL_MAX_LINE (1024).
 * The string contains embedded newlines to exercise \n -> \r\n expansion.
 * Verifies: WriteTerminal returns correct count, stats reflect \r\n expansion.
 */

void writer(void *);

/* Build a 1500-byte string with newlines every 100 chars (14 newlines total) */
static char bigbuf[1501];
static int biglen;

static void
build_big_string(void)
{
    int i;
    for (i = 0; i < 1500; i++) {
        if (i > 0 && i % 100 == 0)
            bigbuf[i] = '\n';
        else
            bigbuf[i] = 'A' + (i % 26);
    }
    bigbuf[1500] = '\0';
    biglen = 1500;
}

int main(int argc, char **argv)
{
    (void)argc;
    (void)argv;

    build_big_string();

    InitTerminalDriver();
    InitTerminal(1);

    ThreadCreate(writer, NULL);
    ThreadWaitAll();

    /* Check statistics */
    struct termstat *stats = malloc(sizeof(struct termstat) * NUM_TERMINALS);
    TerminalDriverStatistics(stats);

    /* user_in should be 1500 (the buflen we passed) */
    assert(stats[1].user_in == 1500);

    /* Count newlines in our string */
    int newlines = 0;
    int i;
    for (i = 0; i < biglen; i++) {
        if (bigbuf[i] == '\n') newlines++;
    }

    /* tty_out should be 1500 + newlines (each \n expands to \r\n) */
    printf("user_in=%d tty_out=%d expected_tty_out=%d newlines=%d\n",
           stats[1].user_in, stats[1].tty_out, biglen + newlines, newlines);
    fflush(stdout);
    assert(stats[1].tty_out == biglen + newlines);

    free(stats);

    sleep(5);
    exit(0);
}

void
writer(void *arg)
{
    (void)arg;
    int status;

    printf("Doing WriteTerminal of %d bytes... ", biglen);
    fflush(stdout);
    status = WriteTerminal(1, bigbuf, biglen);
    printf("Done: status = %d.\n", status);
    fflush(stdout);

    assert(status == biglen);
}
