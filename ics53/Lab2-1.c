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

//Create child processes recursively to search for the number
int arraySearch(int arr[], int numSearch, int start, int end)
{
	if (start == end)
		printf("pid %d, value: %d\n", getpid(), arr[start]);
	if (start == end && arr[start] == numSearch)
	{
		return start;
	}
	else if (start < end)
	{
		int numReturn = -1;
		int mid;
		int status = 0;
		mid = (start + end) / 2;
		int pid;
		
		if ((pid = fork()) == 0)
		{	
			numReturn = arraySearch(arr, numSearch, start, mid);
			if (numReturn == -1)
				exit(EXIT_FAILURE);
			else exit(numReturn);
		}
		else if (pid > 0)
		{
			int num1 = -1, num2 = -1;
			num1 = arraySearch(arr, numSearch, mid + 1, end);
			wait(&status);
			num2 = WEXITSTATUS(status);
			if (WEXITSTATUS(status) != EXIT_FAILURE)
				return num2;
			else return num1;
		}
		else exit(-1);
	}
	else return -1;
}

int main(int argc, char* argv[])
{
    FILE *fp = fopen(argv[1], "r");
    int numSearch = atoi(argv[2]);
    int arr[10];
    char *token;
    char line[1000];
    int i = 0, length = 0;

	//Read file, tokenize strings into array and convert them into integers
    while (fgets(line, 1000, fp) != NULL)
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

	printf("Search output: %d\n", arraySearch(arr, numSearch, 0, length - 1));
	
    return 0;
}
