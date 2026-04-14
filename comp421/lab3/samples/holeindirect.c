#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <comp421/hardware.h>
#include <string.h>

#include <comp421/yalnix.h>
#include <comp421/iolib.h>

#define READBUFSIZE 6500

#include <stdio.h>
#define assert(message, test) do { if (!(test)) { Shutdown(); printf(message); return 1; } } while (0)

int
main()
{
    char *first = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Duis ut diam quam nulla porttitor. Blandit cursus risus at ultrices mi tempus imperdiet nulla. Elit at imperdiet dui accumsan sit amet nulla. Suscipit tellus mauris a diam maecenas sed. Faucibus vitae aliquet nec ullamcorper sit amet risus nullam eget. Orci porta non pulvinar neque laoreet suspendisse interdum consectetur libero. Dictum varius duis at consectetur lorem donec massa sapien. At consectetur lorem donec massa sapien faucibus et molestie ac. Odio tempor orci dapibus ultrices in iaculis nunc sed augue. Et leo duis ut diam quam nulla porttitor massa id. Vel orci porta non pulvinar neque laoreet suspendisse interdum consectetur. Diam sollicitudin tempor id eu nisl nunc mi ipsum faucibus. Vitae aliquet nec ullamcorper sit amet risus nullam eget felis."
        "Fames ac turpis egestas sed tempus urna. Tortor id aliquet lectus proin nibh nisl. Nunc id cursus metus aliquam eleifend. Massa vitae tortor condimentum lacinia quis. Commodo odio aenean sed adipiscing diam donec adipiscing. Ultrices in iaculis nunc sed augue. Mi in nulla posuere sollicitudin aliquam ultrices sagittis orci a. Integer quis auctor elit sed vulputate mi sit amet. Condimentum id venenatis a condimentum. Fermentum odio eu feugiat pretium nibh ipsum. Aenean sed adipiscing diam donec adipiscing tristique risus nec feugiat."
        "Eget est lorem ipsum dolor. Eu turpis egestas pretium aenean pharetra magna. Aliquet nec ullamcorper sit amet risus. Sed enim ut sem viverra aliquet eget. Donec pretium vulputate sapien nec sagittis. Viverra ipsum nunc aliquet bibendum enim facilisis gravida. Eget est lorem ipsum dolor sit amet. Gravida in fermentum et sollicitudin ac. Est lorem ipsum dolor sit amet consectetur adipiscing elit. Neque aliquam vestibulum morbi blandit cursus risus. Ultrices in iaculis nunc sed. Sem fringilla ut morbi tincidunt augue interdum. Varius sit amet mattis vulputate enim nulla aliquet."
        "A diam sollicitudin tempor id eu nisl nunc mi. Integer malesuada nunc vel risus. Amet consectetur adipiscing elit ut. Dui id ornare arcu odio ut sem nulla pharetra diam. Eget felis eget nunc lobortis mattis aliquam faucibus. Diam vulputate ut pharetra sit amet aliquam id diam. Feugiat in ante metus dictum at tempor commodo. Feugiat nisl pretium fusce id velit ut tortor. Ut pharetra sit amet aliquam id diam. Molestie a iaculis at erat pellentesque adipiscing commodo elit at. In nulla posuere sollicitudin aliquam ultrices. Sit amet nisl suscipit adipiscing bibendum est ultricies integer quis. Tortor at auctor urna nunc id cursus metus aliquam eleifend. Nisl nisi scelerisque eu ultrices vitae auctor eu augue ut. Urna neque viverra justo nec ultrices dui sapien eget. Euismod elementum nisi quis eleifend quam. Imperdiet proin fermentum leo vel."
        "Arcu bibendum at varius vel pharetra vel. Commodo viverra maecenas accumsan lacus vel facilisis. Ultrices dui sapien eget mi proin sed libero. Dui id ornare arcu odio. Venenatis urna cursus eget nunc scelerisque viverra mauris in aliquam. In hac habitasse platea dictumst vestibulum rhoncus. Penatibus et magnis dis parturient montes nascetur ridiculus mus. Quisque sagittis purus sit amet. Dui nunc mattis enim ut tellus elementum sagittis. Sed libero enim sed faucibus turpis in eu. Pretium lectus quam id leo in vitae turpis massa."
        "Sodales neque sodales ut etiam sit amet nisl purus. Diam maecenas sed enim ut sem. Ipsum dolor sit amet consectetur adipiscing elit ut aliquam purus. Dictum sit amet justo donec enim diam vulputate. Neque vitae tempus quam pellentesque nec nam aliquam sem et. Dictum at tempor commodo ullamcorper. Pharetra diam sit amet nisl suscipit. Sed vulputate odio ut enim blandit volutpat maecenas. Sit amet porttitor eget dolor morbi non arcu risus quis. Nullam vehicula ipsum a arcu cursus vitae congue mauris rhoncus. Volutpat ac tincidunt vitae semper quis lectus. Velit scelerisque in dictum non consectetur. Orci dapibus ultrices in iaculis nunc sed. Nec sagittis aliquam malesuada bibendum arcu vitae. Tristique et egestas quis ipsum suspendisse ultrices gravida. Nibh cras pulvinar mattis nunc sed."
        "Quam quisque id diam vel quam elementum pulvinar. Semper auctor neque vitae tempus quam. Vitae justo eget magna fermentum iaculis eu non diam phasellus. Vitae congue eu consequat ac. Sit amet justo donec enim diam vulputate. Scelerisque varius morbi enim nunc faucibus a pellentesque. Tellus mauris a diam maecenas sed enim. Quisque egestas diam in arcu cursus euismod. Feugiat in fermentum posuere urna nec tincidunt praesent semper feugiat. Maecenas accumsan lacus vel facilisis."
        "Erat imperdiet sed euismod nisi porta lorem mollis aliquam ut. Senectus et netus et malesuada fames. Sed id semper risus in hendrerit gravida rutrum. Sit amet nisl suscipit adipiscing. Malesuada proin libero nunc consequat interdum varius. In aliquam sem fringilla ut morbi tincidunt augue interdum. Ut tristique et egestas quis. Arcu bibendum at varius vel pharetra vel turpis. Morbi leo urna molestie at elementum. Tincidunt augue interdum velit euismod in pellentesque massa. Metus dictum at tempor commodo ullamcorper. Rhoncus dolor purus non enim praesent elementum facilisis leo. Imperdiet proin fermentum leo vel orci porta non pulvinar neque. Sem viverra aliquet eget sit amet tellus cras.";
    
    char *second = "but not on wednesdays";
    char *third = "because he hates wednesdays";
    char *read_buf = calloc(1, 6500);

    int fd = Create("dave");
    TracePrintf(0, "fd: %d\n", fd);
    assert("Create failed", fd != ERROR);
    assert("Fd not 0", fd == 0);

    int write_bytes = Write(fd, first, strlen(first));
    TracePrintf(0, "bytes written: %d\n", write_bytes);

    TracePrintf(0, "seeked to: %d\n", Seek(fd, 6000, SEEK_SET));

    write_bytes = Write(fd, second, strlen(second));
    TracePrintf(0, "bytes written: %d\n", write_bytes);

    TracePrintf(0, "seeked to: %d\n", Seek(fd, 20, SEEK_CUR));

    write_bytes = Write(fd, third, strlen(third) + 1);
    TracePrintf(0, "bytes written: %d\n", write_bytes);

    TracePrintf(0, "seeked to: %d\n", Seek(fd, 0, SEEK_SET));

    int read_bytes = Read(fd, read_buf, READBUFSIZE);
    TracePrintf(0, "bytes read: %d\n", read_bytes);

    int i;
    for (i = 0; i < READBUFSIZE; i++) {
        TracePrintf(0, "%d %c\n", i, read_buf[i]);
    }

    assert("Read buffer is not the same as the written buffer 1", strcmp(read_buf, first) == 0);
    assert("Read buffer is not the same as the written buffer 2", strcmp(read_buf + 6000, second) == 0);
    assert("Read buffer is not the same as the written buffer 3", strcmp(read_buf + 6020 + strlen(second), third) == 0);
    printf("Test passed\n");

    Shutdown();
    return 0;
}