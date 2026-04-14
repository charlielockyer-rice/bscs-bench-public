/*
 * csapp.c - Stub implementations for CS:APP library
 *
 * Minimal implementations for COMP 321 projects.
 * The full csapp library is from "Computer Systems: A Programmer's Perspective"
 * by Bryant and O'Hallaron.
 */

#include "csapp.h"

/* Error handling functions */
void unix_error(char *msg) {
    fprintf(stderr, "%s: %s\n", msg, strerror(errno));
    exit(1);
}

void posix_error(int code, char *msg) {
    fprintf(stderr, "%s: %s\n", msg, strerror(code));
    exit(1);
}

void dns_error(char *msg) {
    fprintf(stderr, "%s\n", msg);
    exit(1);
}

void gai_error(int code, char *msg) {
    fprintf(stderr, "%s: %s\n", msg, gai_strerror(code));
    exit(1);
}

void app_error(char *msg) {
    fprintf(stderr, "%s\n", msg);
    exit(1);
}

/* Wrappers for memory allocation */
void *Malloc(size_t size) {
    void *p = malloc(size);
    if (p == NULL)
        unix_error("Malloc error");
    return p;
}

void *Realloc(void *ptr, size_t size) {
    void *p = realloc(ptr, size);
    if (p == NULL)
        unix_error("Realloc error");
    return p;
}

void *Calloc(size_t nmemb, size_t size) {
    void *p = calloc(nmemb, size);
    if (p == NULL)
        unix_error("Calloc error");
    return p;
}

void Free(void *ptr) {
    free(ptr);
}

/* Wrappers for Unix I/O */
int Open(const char *pathname, int flags, mode_t mode) {
    int fd = open(pathname, flags, mode);
    if (fd < 0)
        unix_error("Open error");
    return fd;
}

ssize_t Read(int fd, void *buf, size_t count) {
    ssize_t n = read(fd, buf, count);
    if (n < 0)
        unix_error("Read error");
    return n;
}

ssize_t Write(int fd, const void *buf, size_t count) {
    ssize_t n = write(fd, buf, count);
    if (n < 0)
        unix_error("Write error");
    return n;
}

void Close(int fd) {
    if (close(fd) < 0)
        unix_error("Close error");
}

/* Standard I/O wrappers */
FILE *Fopen(const char *filename, const char *mode) {
    FILE *fp = fopen(filename, mode);
    if (fp == NULL)
        unix_error("Fopen error");
    return fp;
}

void Fclose(FILE *fp) {
    if (fclose(fp) != 0)
        unix_error("Fclose error");
}

size_t Fread(void *ptr, size_t size, size_t nmemb, FILE *stream) {
    size_t n = fread(ptr, size, nmemb, stream);
    if (ferror(stream))
        unix_error("Fread error");
    return n;
}

void Fwrite(const void *ptr, size_t size, size_t nmemb, FILE *stream) {
    if (fwrite(ptr, size, nmemb, stream) < nmemb)
        unix_error("Fwrite error");
}

/* Rio (Robust I/O) package */
ssize_t rio_readn(int fd, void *usrbuf, size_t n) {
    size_t nleft = n;
    ssize_t nread;
    char *bufp = usrbuf;

    while (nleft > 0) {
        if ((nread = read(fd, bufp, nleft)) < 0) {
            if (errno == EINTR)
                nread = 0;
            else
                return -1;
        } else if (nread == 0)
            break;
        nleft -= nread;
        bufp += nread;
    }
    return (n - nleft);
}

ssize_t rio_writen(int fd, void *usrbuf, size_t n) {
    size_t nleft = n;
    ssize_t nwritten;
    char *bufp = usrbuf;

    while (nleft > 0) {
        if ((nwritten = write(fd, bufp, nleft)) <= 0) {
            if (errno == EINTR)
                nwritten = 0;
            else
                return -1;
        }
        nleft -= nwritten;
        bufp += nwritten;
    }
    return n;
}

void rio_readinitb(rio_t *rp, int fd) {
    rp->rio_fd = fd;
    rp->rio_cnt = 0;
    rp->rio_bufptr = rp->rio_buf;
}

static ssize_t rio_read(rio_t *rp, char *usrbuf, size_t n) {
    int cnt;

    while (rp->rio_cnt <= 0) {
        rp->rio_cnt = read(rp->rio_fd, rp->rio_buf, sizeof(rp->rio_buf));
        if (rp->rio_cnt < 0) {
            if (errno != EINTR)
                return -1;
        } else if (rp->rio_cnt == 0)
            return 0;
        else
            rp->rio_bufptr = rp->rio_buf;
    }

    cnt = n;
    if ((size_t)rp->rio_cnt < n)
        cnt = rp->rio_cnt;
    memcpy(usrbuf, rp->rio_bufptr, cnt);
    rp->rio_bufptr += cnt;
    rp->rio_cnt -= cnt;
    return cnt;
}

ssize_t rio_readnb(rio_t *rp, void *usrbuf, size_t n) {
    size_t nleft = n;
    ssize_t nread;
    char *bufp = usrbuf;

    while (nleft > 0) {
        if ((nread = rio_read(rp, bufp, nleft)) < 0)
            return -1;
        else if (nread == 0)
            break;
        nleft -= nread;
        bufp += nread;
    }
    return (n - nleft);
}

ssize_t rio_readlineb(rio_t *rp, void *usrbuf, size_t maxlen) {
    size_t n;
    int rc;
    char c, *bufp = usrbuf;

    for (n = 1; n < maxlen; n++) {
        if ((rc = rio_read(rp, &c, 1)) == 1) {
            *bufp++ = c;
            if (c == '\n') {
                n++;
                break;
            }
        } else if (rc == 0) {
            if (n == 1)
                return 0;
            else
                break;
        } else
            return -1;
    }
    *bufp = 0;
    return n - 1;
}

/* Client/server helper functions */
int open_clientfd(char *hostname, char *port) {
    int clientfd;
    struct addrinfo hints, *listp, *p;

    memset(&hints, 0, sizeof(struct addrinfo));
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_NUMERICSERV | AI_ADDRCONFIG;
    getaddrinfo(hostname, port, &hints, &listp);

    for (p = listp; p; p = p->ai_next) {
        if ((clientfd = socket(p->ai_family, p->ai_socktype, p->ai_protocol)) < 0)
            continue;
        if (connect(clientfd, p->ai_addr, p->ai_addrlen) != -1)
            break;
        close(clientfd);
    }

    freeaddrinfo(listp);
    if (!p)
        return -1;
    return clientfd;
}

int open_listenfd(char *port) {
    struct addrinfo hints, *listp, *p;
    int listenfd, optval = 1;

    memset(&hints, 0, sizeof(struct addrinfo));
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE | AI_ADDRCONFIG | AI_NUMERICSERV;
    getaddrinfo(NULL, port, &hints, &listp);

    for (p = listp; p; p = p->ai_next) {
        if ((listenfd = socket(p->ai_family, p->ai_socktype, p->ai_protocol)) < 0)
            continue;
        setsockopt(listenfd, SOL_SOCKET, SO_REUSEADDR, (const void *)&optval, sizeof(int));
        if (bind(listenfd, p->ai_addr, p->ai_addrlen) == 0)
            break;
        close(listenfd);
    }

    freeaddrinfo(listp);
    if (!p)
        return -1;
    if (listen(listenfd, LISTENQ) < 0) {
        close(listenfd);
        return -1;
    }
    return listenfd;
}

/* Process control wrappers */
pid_t Fork(void) {
    pid_t pid = fork();
    if (pid < 0)
        unix_error("Fork error");
    return pid;
}

void Kill(pid_t pid, int signum) {
    if (kill(pid, signum) < 0)
        unix_error("Kill error");
}

/* Signal wrappers */
handler_t *Signal(int signum, handler_t *handler) {
    struct sigaction action, old_action;

    action.sa_handler = handler;
    sigemptyset(&action.sa_mask);
    action.sa_flags = SA_RESTART;

    if (sigaction(signum, &action, &old_action) < 0)
        unix_error("Signal error");
    return old_action.sa_handler;
}

void Sigprocmask(int how, const sigset_t *set, sigset_t *oldset) {
    if (sigprocmask(how, set, oldset) < 0)
        unix_error("Sigprocmask error");
}

void Sigemptyset(sigset_t *set) {
    if (sigemptyset(set) < 0)
        unix_error("Sigemptyset error");
}

void Sigfillset(sigset_t *set) {
    if (sigfillset(set) < 0)
        unix_error("Sigfillset error");
}

void Sigaddset(sigset_t *set, int signum) {
    if (sigaddset(set, signum) < 0)
        unix_error("Sigaddset error");
}

/* Socket wrappers */
int Socket(int domain, int type, int protocol) {
    int fd = socket(domain, type, protocol);
    if (fd < 0)
        unix_error("Socket error");
    return fd;
}

int Accept(int s, struct sockaddr *addr, socklen_t *addrlen) {
    int fd = accept(s, addr, addrlen);
    if (fd < 0)
        unix_error("Accept error");
    return fd;
}

void Connect(int sockfd, struct sockaddr *serv_addr, int addrlen) {
    if (connect(sockfd, serv_addr, addrlen) < 0)
        unix_error("Connect error");
}

void Inet_ntop(int af, const void *src, char *dst, socklen_t size) {
    if (inet_ntop(af, src, dst, size) == NULL)
        unix_error("Inet_ntop error");
}

/* Thread wrappers */
void Pthread_create(pthread_t *tidp, pthread_attr_t *attrp, void *(*routine)(void *), void *argp) {
    int rc = pthread_create(tidp, attrp, routine, argp);
    if (rc != 0)
        posix_error(rc, "Pthread_create error");
}

void Pthread_detach(pthread_t tid) {
    int rc = pthread_detach(tid);
    if (rc != 0)
        posix_error(rc, "Pthread_detach error");
}

pthread_t Pthread_self(void) {
    return pthread_self();
}

/* Semaphore wrappers */
/* Note: sem_init is deprecated on macOS, using named semaphores instead */
#ifdef __APPLE__
#include <dispatch/dispatch.h>
/* On macOS, we use dispatch semaphores as sem_init is deprecated */
void Sem_init(sem_t *sem, int pshared, unsigned int value) {
    (void)sem; (void)pshared; (void)value;
    /* Stub - use dispatch_semaphore_create on macOS if needed */
}
#else
void Sem_init(sem_t *sem, int pshared, unsigned int value) {
    if (sem_init(sem, pshared, value) < 0)
        unix_error("Sem_init error");
}
#endif

void P(sem_t *sem) {
    if (sem_wait(sem) < 0)
        unix_error("P error");
}

void V(sem_t *sem) {
    if (sem_post(sem) < 0)
        unix_error("V error");
}
