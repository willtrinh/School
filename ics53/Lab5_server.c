/*
 * Will Trinh (ID: 17986840)
 * Partner: Son Le
 * Lab 5 - Socket Programming
*/

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdint.h>
#include <netdb.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define MAXLINE 256
#define LISTENQ 10

//function from lecture slides
int open_listenfd(int port)
{
    int listenfd, optval = 1;
    struct sockaddr_in serveraddr;

    //create socket descriptor
    if ((listenfd = socket(AF_INET, SOCK_STREAM, 0)) < 0)
        return -1;

    //eliminates "address already in sue" error from bind
    if (setsockopt(listenfd, SOL_SOCKET, SO_REUSEADDR,
                   (const void *) &optval, sizeof(int)) < 0)
        return -1;

    bzero((char *) &serveraddr, sizeof(serveraddr));
    serveraddr.sin_family = AF_INET;
    serveraddr.sin_addr.s_addr = htonl(INADDR_ANY);
    serveraddr.sin_port = htons((unsigned short) port);
    if (bind(listenfd, (struct sockaddr *) &serveraddr, sizeof(serveraddr)) < 0)
        return -1;

    if (listen(listenfd, LISTENQ) < 0)
        return -1;
    return listenfd;
}

int main(int argc, char **argv)
{
    //buffer to hold input received from client and buffer to hold address
    char message[MAXLINE];
    char addr[MAXLINE - 1];

    /*code from lecture slides */
    int listenfd, connfd, port, clientlen;
    struct sockaddr_in clientaddr;

    port = atoi(argv[1]);
    listenfd = open_listenfd(port);

    while (1)
    {
        clientlen = sizeof(clientaddr);
        connfd = accept(listenfd, (struct sockaddr *) &clientaddr, &clientlen);
    /*end of code from lecture slides */

        printf("Address server started\n");
        while (read(connfd, message, MAXLINE - 1) > 0)
        {
            int i = 0;
            for (i = 0; i < (int) message[0]; i++)
                addr[i] = message[i + 1];
            addr[i] = '\0';

            //print received input
            printf("%s\n", addr);

            if (strcmp(addr, "harris@ics.uci.edu") == 0)
            {
                message[0] = (unsigned) strlen("Ian G. Harris");
                strcpy(addr, "Ian G. Harris");
            }
            else if (strcmp(addr, "joe@cnn.com") == 0)
            {
                message[0] = (unsigned) strlen("Joe Smith");
                strcpy(addr, "Joe Smith");
            }
            else if (strcmp(addr, "jane@slashdot.org") == 0)
            {
                message[0] = (unsigned) strlen("Jane Smith");
                strcpy(addr, "Jane Smith");
            }
            else if (strcmp(addr, "+++") == 0)
                break;
            else
            {
                message[0] = (unsigned) strlen("unknown");
                strcpy(addr, "unknown");
            }
            message[1] = '\0';

            strcat(message, addr);
            //send back name found/unknown if not found
            write(connfd, message, strlen(message) * sizeof(char));

            //read last byte
            read(connfd, message, 1);
        }
		close(connfd);
    }
    //exit
    return 0;
}
