#include <comp421/yalnix.h>
#include <stdio.h>

void
force(char *addr)
{
    *addr = 42;
}

int
main()
{
    char big_buffer[20*1024];
    int foo;
    int i;

    foo = 42;
    printf("foo = %d\n", foo);
    for (i = 0; i < (signed) sizeof(big_buffer); i++) 
	force(big_buffer + i);

    Exit(0);
}
