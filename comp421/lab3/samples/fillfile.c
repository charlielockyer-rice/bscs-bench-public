#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <comp421/hardware.h>
#include <string.h>

#include <comp421/yalnix.h>
#include <comp421/iolib.h>

#define BLOCKSIZE 512

int
main()
{
        char *largebuf = "zSsernHBqug4pnoVj9bqAcPV5CENiSBY4aDoPmgYwcvO9uqLS8qMZL2EwSUX1BxaHhFm1SLo8hmOgoiqtgezUExMdeN8KFBcAqhpIn7SriT2DkJbct2CT2rqTtf5VjtabONwoEikOuAAmfK3fcCgA3CpEZoulWr19R8PXqtJ1oSLc7xMx4sFwVU2fIKDCMkhy6t51T1VTiEpUcljotJUKUeVS3Pvt0eUUIkqOkwKhU6j78vwcGg6viRgTxfPZZ8lr69PUB52WQzIjjqWW5vTDU7S73zLk7FtLWVZYmFzBNussqgzWLks3dnsHsfd4e3BxzT9dTzbusBZvp5rTXcKVWZWpQM9F56IWRuaIUt26Ai3ZPrUjVhiyGyCTWJTORs0U9XrmwHMSUCivomUDT7LoLfrJHBnW5mTzC9si4loBW2w6md7YjqMSsDuDM0sjhV0IjFwcSXgETDhTvxrH8ZK7hBtt0drtMK0VCxnHvPgEMbpPtdbkFPJu6lXcz3B4zhZ";

        MkDir("large files inside");
        int fd = Create("large files inside/largefile");
        TracePrintf(0, "create: %d\n", fd);

        int write_bytes = 1;
        while (write_bytes > 0) {
                write_bytes = Write(fd, largebuf, BLOCKSIZE);
        }

        TracePrintf(0, "done writing\n");

        Shutdown();
        return 0;
}