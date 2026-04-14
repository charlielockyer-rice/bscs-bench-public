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
    char *first = "john is a silly man";
    char *second = "but not on wednesdays";
    char *third = "because he hates wednesdays";
    char *fourth = "hehehehehehe lol";

    int hole1_size = 10;
    int hole2_size = 1;
    int hole3_size = BLOCKSIZE * 2;

    int read_buf_size = strlen(first) + strlen(second) + strlen(third) + strlen(fourth) + hole1_size + hole2_size + hole3_size;
    char *read_buf = malloc(read_buf_size);

    int fd = Create("cox");
    TracePrintf(0, "fd: %d\n", fd);

    int write_bytes = Write(fd, first, strlen(first));
    TracePrintf(0, "bytes written: %d\n", write_bytes);

    TracePrintf(0, "seeked to: %d\n", Seek(fd, hole1_size, SEEK_CUR));

    write_bytes = Write(fd, second, strlen(second));
    TracePrintf(0, "bytes written: %d\n", write_bytes);

    TracePrintf(0, "seeked to: %d\n", Seek(fd, hole2_size, SEEK_CUR));

    write_bytes = Write(fd, third, strlen(third));
    TracePrintf(0, "bytes written: %d\n", write_bytes);

    TracePrintf(0, "seeked to: %d\n", Seek(fd, hole3_size, SEEK_CUR));

    write_bytes = Write(fd, fourth, strlen(fourth));
    TracePrintf(0, "bytes written: %d\n", write_bytes);

    TracePrintf(0, "seeked to: %d\n", Seek(fd, 0, SEEK_SET));

    int read_bytes = Read(fd, read_buf, read_buf_size);
    TracePrintf(0, "bytes read: %d\n", read_bytes);

    int i;
    for (i = 0; i < read_buf_size; i++) {
        TracePrintf(0, "%d %c\n", i, read_buf[i]);
    }

    Shutdown();
    return 0;
}