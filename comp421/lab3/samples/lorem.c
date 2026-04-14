#include <comp421/hardware.h>
#include <comp421/iolib.h>
#include <malloc.h>
#include <string.h>

#include <stdio.h>
#define assert(message, test) do { if (!(test)) { Shutdown(); printf(message); return 1; } } while (0)

int main(int argc, char **argv) {
        (void) argc;
        (void) argv;
        
        int fd = Create("lorem");
        char *silliest = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Suscipit tellus mauris a diam maecenas sed enim. Augue interdum velit euismod in pellentesque massa placerat. Vitae congue mauris rhoncus aenean vel elit scelerisque. Vitae suscipit tellus mauris a diam maecenas sed enim. Nunc consequat interdum varius sit. Euismod nisi porta lorem mollis. Sem et tortor consequat id porta nibh venenatis cras. Enim eu turpis egestas pretium. In arcu cursus euismod quis viverra. Non diam phasellus vestibulum lorem sed risus. Gravida arcu ac tortor dignissim convallis aenean et tortor. In aliquam sem fringilla ut morbi. Dui id ornare arcu odio ut. Proin libero nunc consequat interdum. Dolor sit amet consectetur adipiscing. Phasellus faucibus scelerisque eleifend donec pretium vulputate sapien. Facilisis leo vel fringilla est ullamcorper eget. Eget magna fermentum iaculis eu non diam. Aliquam faucibus purus in massa tempor. Aliquam sem et tortor consequat id porta nibh venenatis.";
        char *read_buf = malloc(strlen(silliest) + 1);
        int write_bytes = Write(fd, silliest, strlen(silliest));
        Seek(fd, 0, SEEK_SET);
        TracePrintf(0, "write_bytes: %d\n", write_bytes);
        int read_bytes = Read(fd, read_buf, write_bytes);
        TracePrintf(0, "buffer read %d bytes: %.*s\n", read_bytes, read_bytes, read_buf);

        assert("Read and write bytes do not match\n", write_bytes == read_bytes);
        assert("Read and write buffers do not match\n", strcmp(silliest, read_buf) == 0);
        printf("Test passed\n");
        Shutdown();

        return 0;
}