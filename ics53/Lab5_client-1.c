/*
 * Will Trinh (ID: 17986840)
 * Partner: Son Le
 * Lab 5 - Socket Programming
 * March 18, 2019
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

int open_clientfd(char *hostName, int port)
{
    int clientfd; //socket descriptor
    struct hostent *hp; //DNS host entry
    struct sockaddr_in serveraddr; //server's IP address

    if ((clientfd = socket(AF_INET, SOCK_STREAM, 0)) < 0)
        return -1; //check errno for cause of error

    //fill in server's IP and port
    if ((hp = gethostbyname(hostName)) == NULL)
        return -2; //check errno for cause of error
    bzero ((char *) &serveraddr, sizeof(serveraddr));
    serveraddr.sin_family = AF_INET;
    bcopy((char *) hp->h_addr_list[0],
          (char *) &serveraddr.sin_addr.s_addr, hp->h_length);
    serveraddr.sin_port = htons(port);

    //establish connection with server
    if (connect(clientfd, (struct sockaddr *) &serveraddr, sizeof(serveraddr)) < 0)
        return -1;
    return clientfd;
}

int main(int argc, char **argv)
{   
    int clientfd;
    char *hostName = argv[1];
    int port = atoi(argv[2]);
	char delimiters[] = " \r\n\f\v\t";
    char input[MAXLINE];
    char *token;
    int quit = 0;
    char buf[MAXLINE], response[MAXLINE], output[MAXLINE - 1];

    //connect server & client
    clientfd = open_clientfd(hostName, port);

    //loop
    while (quit == 0)
    {
        printf(">");
        fgets(input, MAXLINE, stdin);
        token = strtok(input, delimiters);

        if (token != NULL)
        {
            if (strcmp(token, "+++") == 0)
                quit = 1;

            buf[0] = (unsigned)strlen(token);
            buf[1] = '\0';
            strcat(buf, token);
            write(clientfd, buf, sizeof(buf));

            if (quit == 0)
            {
                //receive line from server
                read(clientfd, response, sizeof(response));

                int i = 0;

                for (i = 0; i < (int) response[0]; i++)
                    output[i] = response[i + 1];

                output[i] = '\0';

                //print server response
                printf("%s\n", output);
            }
        }
    }
    //exit
	close(clientfd);
    return 0;
}
