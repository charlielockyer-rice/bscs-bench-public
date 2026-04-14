#include <comp421/hardware.h>
#include <stdio.h>

int
main()
{
    printf("IDLE\n");
    TracePrintf(0, "IDLE\n");
    while (1) {
        // printf("IDLE is paused\n");
        Pause();
    }
}