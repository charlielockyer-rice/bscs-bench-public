#include <threads.h>
#include <hardware.h>
#include <terminals.h>

/*
 * COMP 421 Lab 1 - MonTTY Terminal Device Driver
 *
 * All 7 entry procedures of the monitor are stubbed below.
 * Replace these stubs with your implementation.
 *
 * Section 6.1: ReceiveInterrupt, TransmitInterrupt
 * Section 6.2: WriteTerminal, ReadTerminal, InitTerminal,
 *              TerminalDriverStatistics, InitTerminalDriver
 */

int
InitTerminalDriver(void)
{
    return 0;
}

int
InitTerminal(int term)
{
    (void)term;
    return 0;
}

int
ReadTerminal(int term, char *buf, int buflen)
{
    (void)term;
    (void)buf;
    (void)buflen;
    return -1;
}

int
WriteTerminal(int term, char *buf, int buflen)
{
    (void)term;
    (void)buf;
    (void)buflen;
    return -1;
}

int
TerminalDriverStatistics(struct termstat *stats)
{
    (void)stats;
    return -1;
}

void
ReceiveInterrupt(int term)
{
    (void)term;
}

void
TransmitInterrupt(int term)
{
    (void)term;
}
