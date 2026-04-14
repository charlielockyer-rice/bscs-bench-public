#include <comp421/iolib.h>
#include <comp421/hardware.h>
#include <string.h>

int main(int argc, char **argv) {
        (void) argc;
        (void) argv;

       char *silly = "Hello world!";
        char read_buf[80] = "old stuff";
     //   int status = MkDir("/my_dir");
        //TracePrintf(0, "MkDir returned with status %d\n", status);
        // int fd = Create("/my_dir/wide");
        // TracePrintf(0, "new fd: %d\n", fd);
        // int write_bytes = Write(fd, silly, strlen(silly));
        // TracePrintf(0, "bytes written: %d\n", write_bytes);
        // int seek_pos = Seek(fd, 0, SEEK_SET);
        // TracePrintf(0, "seeked to %d\n", seek_pos);
        // int read_bytes = Read(fd, read_buf, write_bytes);
        // TracePrintf(0, "buffer read %d bytes: %.*s\n", read_bytes, read_bytes, read_buf);
        // Create("/my_dir/wide");
        // read_bytes = Read(fd, read_buf, write_bytes);
        // TracePrintf(0, "buffer read %d bytes: %.*s\n", read_bytes, read_bytes, read_buf);
        // TracePrintf(0, "hmm\n");
        // Create("/my_dir/wide");
        // read_bytes = Read(fd, read_buf, write_bytes);
        // TracePrintf(0, "buffer read %d bytes: %.*s\n", read_bytes, read_bytes, read_buf);
        // TracePrintf(0, "hmm\n");



        int fd = Create("wide");
        TracePrintf(0, "new fd: %d\n", fd);
        int write_bytes = Write(fd, silly, strlen(silly));
        TracePrintf(0, "bytes written: %d\n", write_bytes);
        int seek_pos = Seek(fd, 0, SEEK_SET);
        TracePrintf(0, "seeked to %d\n", seek_pos);
        int read_bytes = Read(fd, read_buf, write_bytes);
        TracePrintf(0, "buffer read %d bytes: %.*s\n", read_bytes, read_bytes, read_buf);
        
        fd = Create("wide");
        write_bytes = Write(fd, "Yo mama", 4);
        TracePrintf(0, "bytes written: %d\n", write_bytes);
        seek_pos = Seek(fd, 0, SEEK_SET);
        TracePrintf(0, "seeked to %d\n", seek_pos);
        read_bytes = Read(fd, read_buf, write_bytes);
        TracePrintf(0, "buffer read %d bytes: %.*s\n", read_bytes, read_bytes, read_buf);

        TracePrintf(0, "hmm\n");
        seek_pos = Seek(fd, -2, SEEK_END);
        write_bytes = Write(fd, "f", 1);
        seek_pos = Seek(fd, 0, SEEK_SET);
        TracePrintf(0, "bytes written: %d\n", write_bytes);
        read_bytes = Read(fd, read_buf, 4);
        TracePrintf(0, "buffer read %d bytes: %.*s\n", read_bytes, read_bytes, read_buf);
        TracePrintf(0, "hmm\n");

        // char *silliest = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Suscipit tellus mauris a diam maecenas sed enim. Augue interdum velit euismod in pellentesque massa placerat. Vitae congue mauris rhoncus aenean vel elit scelerisque. Vitae suscipit tellus mauris a diam maecenas sed enim. Nunc consequat interdum varius sit. Euismod nisi porta lorem mollis. Sem et tortor consequat id porta nibh venenatis cras. Enim eu turpis egestas pretium. In arcu cursus euismod quis viverra. Non diam phasellus vestibulum lorem sed risus. Gravida arcu ac tortor dignissim convallis aenean et tortor. In aliquam sem fringilla ut morbi. Dui id ornare arcu odio ut. Proin libero nunc consequat interdum. Dolor sit amet consectetur adipiscing. Phasellus faucibus scelerisque eleifend donec pretium vulputate sapien. Facilisis leo vel fringilla est ullamcorper eget. Eget magna fermentum iaculis eu non diam. Aliquam faucibus purus in massa tempor. Aliquam sem et tortor consequat id porta nibh venenatis.";
        // write_bytes = Write(fd, silliest, strlen(silliest));
        // seek_pos = Seek(fd, 12, SEEK_CUR);
        // read_bytes = Read(fd, read_buf, write_bytes);
        // TracePrintf(0, "buffer read %d bytes: %.*s\n", read_bytes, read_bytes, read_buf);

        Shutdown();
        return 0;
}