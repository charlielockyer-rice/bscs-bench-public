#include <comp421/hardware.h>
#include <comp421/yalnix.h>

/*
 * KernelStart - Initialize the Yalnix kernel.
 *
 * Called by the hardware boot sequence to initialize the kernel.
 * Must set up virtual memory, create the idle and init processes,
 * set up trap handlers, and begin running user processes.
 */
void
KernelStart(ExceptionInfo *info, unsigned int pmem_size,
            void *orig_brk, char **cmd_args)
{
    (void)info;
    (void)pmem_size;
    (void)orig_brk;
    (void)cmd_args;

    TracePrintf(0, "KernelStart: stub implementation\n");

    /* TODO: Implement kernel initialization */
}

/*
 * SetKernelBrk - Adjust the kernel heap break.
 *
 * Called by malloc/free in the kernel to grow/shrink the heap.
 * Before virtual memory is enabled, simply track the break.
 * After VM is enabled, must allocate/free physical pages.
 */
int
SetKernelBrk(void *addr)
{
    (void)addr;

    /* TODO: Implement kernel break management */
    return 0;
}
