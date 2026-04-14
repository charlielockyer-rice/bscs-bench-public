#include <stdio.h>
#include <comp421/hardware.h>
#include <comp421/yalnix.h>
#include <comp421/iolib.h>
#include <unistd.h>
#include <stdlib.h>
/* After running this, try topen2 and/or tunlink2 */

void printStat(struct Stat* stat_buf);
int
main()
{
	printf("\n%d\n\n", MkDir("/foo"));
	printf("\n%d\n\n", Create("/bar"));
	//printf("\n%d\n\n", Create("/foo"));
	printf("\n%d\n\n", Create("/foo/zoo"));
    struct Stat* stat_buf = malloc(sizeof(struct Stat*));
    Stat("/foo/zzz", stat_buf);
    printStat(stat_buf);
    Stat("/foo", stat_buf);
    printStat(stat_buf);
	Shutdown();
	return 0;
}
void printStat(struct Stat* stat_buf) {
    TracePrintf(0, "stat_buf- type: %d, inum: %d, size: %d, nlink: %d\n", stat_buf->type, stat_buf->inum, stat_buf->size, stat_buf->nlink);
}