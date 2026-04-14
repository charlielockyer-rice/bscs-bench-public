#include <comp421/hardware.h>
#include <comp421/yalnix.h>
#include <stdio.h>

int
main()
{

    TracePrintf(1, "init: printing!\n");
    printf("init: printing!\n");
    // print the pid
    printf("init: pid: %d\n", GetPid());

    return 0;
}