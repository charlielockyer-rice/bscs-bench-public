#include <comp421/iolib.h>
#include <comp421/filesystem.h>
#include <comp421/yalnix.h>
#include <stdio.h>
#include <string.h>

#define assert(message, test) do { if (!(test)) { Shutdown(); printf(message); return 1; } } while (0)

int main()
{
    int fd;
    int success;
    char writeBuf[4];
    char readBuf2[9];
    writeBuf[0] = 'S';
    writeBuf[1] = 'S';
    writeBuf[2] = 'S';
    writeBuf[3] = 'S';

    char files[17] = "qwertyuioplkjhgf";

    fd = Create("a");
    assert("Failed simple open a\n", fd == 0);
    printf("fd: %i\n", fd);
    Create("b");


    success = MkDir("a");
    assert("Failed Mkdir on existing file\n", success == ERROR);

    success = MkDir("aa");
    assert("Failed simple mkdir\n", success == 0);

    success = Unlink("a");
    assert("Failed unlink\n", success == 0);
    Close(0);

    fd = Create("b");
    printf("fd: %i\n", fd);
    assert("Make after unlink failed\n", fd == 0);

    success = Write(0, writeBuf, 4);
    assert("Write to file failed\n", success == 4);

    Seek(0, 0, 0);

    success = Read(0, readBuf2, 4);
    assert("Read failed\n", success == 4);
    assert("Wrong string in read buffer\n", strncmp(readBuf2, "SSSS", 4) == 0);

    success = Write(0, writeBuf, 4);
    assert("Write to file failed\n", success == 4);

    Seek(0, 0, 0);

    success = Read(0, readBuf2, 8);
    assert("Wrong string in read buffer\n", strncmp(readBuf2, "SSSSSSSS", 8) == 0);

    int i;
    for (i = 0; i < 14; i++) {
        fd = Create((char *)&files[i]);
        printf("creating %s at index %i, resulting in fd %i\n", (char *)&files[i], i, fd);
        assert("Random file not created correctly\n", fd == i + 2);
    }

    fd = Create((char *)&files[14]);
    assert("Tried to create a file with 16 files open\n", fd == -1);

    Seek(0, 0, 0);

    success = Read(0, readBuf2, 4);
    assert("Read failed\n", success == 4);
    assert("Wrong string in read buffer\n", strncmp(readBuf2, "SSSS", 4) == 0);

	printf("All tests passed\n");
	return Shutdown();
}
