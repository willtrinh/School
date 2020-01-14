/*
	Will Trinh
	ICS 53
	Lab 2
*/
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/wait.h>

int arraySearch(int arr[], int numSearch, int start, int end)
{
	if (start == end)
	{
		if (*(arr + end) == numSearch)
		{
			return end;
		}
		exit(0);
	}
	else
	{
		pid_t pid = fork();
		if (pid == 0) return arraySearch(arr, numSearch, start, (start + end) / 2);
		else return arraySearch(arr, numSearch, (start + end) / 2 + 1, end);
	}
	return 0;
}

void printOutput(int arr[], int numSearch, int size)
{
	int i = 0;
	for (i = 0; i < size; i++)
	{
		pid_t cpid = fork();
		if (cpid == 0)
		{
			printf("pid %d, value: %d\n", getpid(), arr[i]); 
			exit(0);
		}
		else
			wait(NULL);
	}
}

int main(int argc, char* argv[])
{
    FILE *fp = fopen(argv[1], "r");
    int num = atoi(argv[2]);
    int arr[10];
    char *token;
    char line[100];
    int i = 0, length = 0;
	int found = -1;
    while (fgets(line, 100, fp) != NULL)
    {
        token = strtok(line, " ");
        arr[i] = atoi(token);
        while (token != NULL)
        {
            arr[i] = atoi(token);
            token = strtok(NULL, " ");
            length++;
            i++;
        }
    }
    fclose(fp);
 
	for (i = 0; i < length; i++)
	{
		if (num == arr[i])
		{
			found = 1;
			break;
		}
		else found = -1;
	}
	
	printOutput(arr,num,length);
	if (found == -1)
	{
		printf("Search output: -1\n");
	}
	else printf("Search output: %d\n", arraySearch(arr, num, 0, length));
	
    return 0;
}
