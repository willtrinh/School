/*
    Son Le, Will Trinh
    Lecture B
    ICS 53 Lab 3
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define COMMAND_LINE_LENGTH 256
#define VM_PAGES 8
#define M_PAGES 4
#define READ "read"
#define WRITE "write"
#define SHOW_MAIN "showmain"
#define SHOW_DISK "showdisk"
#define SHOW_PTABLE "showptable"
#define QUIT "quit"

/******************************************
 * Reset the virtual memory
 ******************************************/
void initMemory(int table[], int maxPages)
{
    int i;
    for(i=0; i<maxPages*2; i++)
        table[i] = -1;
}

/******************************************
 * Reset the virtual memory
 ******************************************/
void initPTable(int table[][4], int maxPages)
{
    int i;
    for(i=0; i<maxPages; i++)
    {
        table[i][0] = i;
        table[i][1] = 0;
        table[i][2] = 0;
        table[i][3] = i;
    }
}

/******************************************
 * Read function
 ******************************************/
void read(int content[][4], int main[], int disk[], int idx, int *curMainPage, int count)
{
    if(count == 2)
    {
        int page = idx/2;
        int offset = idx%2;
        //printf("Valid: %i -- PN: %i\n", content[page][1], content[page][3]);

        if(content[page][1] == 0)
        {
            int i;
            for(i=0; i<VM_PAGES; i++)
            {
                if(content[i][3] == *curMainPage  && content[i][1] == 1)
                {
                    content[i][1] = 0;
                    content[i][3] = i;
                    break;
                }
            }

            // copy to main memory
            int curMainPageIndex = *curMainPage*2;
            int curPageIndex = page*2;
            main[curMainPageIndex]     = disk[curPageIndex];
            main[curMainPageIndex+1]   = disk[curPageIndex+1];
            content[page][1] = 1;
            content[page][3] = *curMainPage;

            // update the current page of main memory
            *curMainPage = *curMainPage+1;
            if(*curMainPage == 4)
                *curMainPage = 0;
        }
            //print out data of the address
            int index = content[page][3]*2 + offset;
            fprintf(stdout, "%i\n", main[index]);
        }
    else
        fprintf(stdout, "Invalid command! The command takes one argument.\n");
}

/******************************************
 * Write function
 ******************************************/
void write(int table[], int idx, int value, int count)
{
    if(count != 3)
        fprintf(stdout, "Invalid command! The command takes two arguments.\n");
    else if(value < 1)
        fprintf(stdout, "Invalid data! The value must be a positive integer.\n");
    else
        table[idx] = value;
}

/******************************************
 * Show main function
 ******************************************/
void show_main(int table[], int page, int count)
{
    if(count != 2)
        fprintf(stdout, "Invalid command! The command takes one argument.\n");
    else if(page<0 || page>3)
        fprintf(stdout, "Invalid argument! The page number is 0 to 3.\n");
    else
    {
        fprintf(stdout, "Address   Contents\n");
        fprintf(stdout, "%-10i %-9i\n", page*2, table[page*2]);
        fprintf(stdout, "%-10i %-9i\n", page*2+1,table[page*2+1]);
    }
}

/******************************************
 * Show disk function
 ******************************************/
void show_disk(int table[], int page, int count)
{
    if(count != 2)
        fprintf(stdout, "Invalid command! The command takes one argument.\n");
    else if(page<0 || page>7)
        fprintf(stdout, "Invalid argument! The page number is 0 to 7.\n");
    else
    {
        fprintf(stdout, "Address   Contents\n");
        fprintf(stdout, "%-10i %-9i\n", page*2, table[page*2]);
        fprintf(stdout, "%-10i %-9i\n", page*2+1,table[page*2+1]);
    }
}

/******************************************
 * Show pTable function
 * Show pTable function
 ******************************************/
void show_ptable(int table[][4], int count)
{
    if(count == 1)
    {
        fprintf(stdout, "VPageNum  Valid     Dirty     PN\n" );
        int i;
        for(i=0; i<VM_PAGES; i++)
        {
            fprintf(stdout, "%-9i %-9i %-9i %-9i\n", table[i][0], table[i][1], table[i][2], table[i][3]);
        }
    }
    else
        fprintf(stdout, "Invalid command! The command does not need argument.\n");
}

/******************************************
 * Main function
 ******************************************/
int main()
{
    char commandLine[COMMAND_LINE_LENGTH];
    char *arrCommand[COMMAND_LINE_LENGTH];
    int countArr, curMainPage=0;
    int isQuit = 0;
    int virtualMemory[VM_PAGES*2];
    int mainMemory[M_PAGES*2];
    int tableContents[VM_PAGES][4];

    initMemory(virtualMemory, VM_PAGES);
    initMemory(mainMemory, VM_PAGES);
    initPTable(tableContents, VM_PAGES);
    while(isQuit == 0)
    {
        countArr = 0;

        /******************************************
         * Read a new command line
         ******************************************/
        printf("$ ");
        fgets(commandLine, COMMAND_LINE_LENGTH, stdin);

        /******************************************
         * Trim the command  and parameters
         ******************************************/
        char *token = strtok(commandLine, " \n\0");
        while(token != NULL){
            arrCommand[countArr] = token;
            countArr++;
            token = strtok(NULL, " ");
        }

        /******************************************
         * Commands call
         ******************************************/
        if(strcmp(arrCommand[0], READ) == 0)
            read(tableContents, mainMemory, virtualMemory, atoi(arrCommand[1]), &curMainPage, countArr);
        else if(strcmp(arrCommand[0], WRITE) == 0)
            write(virtualMemory, atoi(arrCommand[1]), atoi(arrCommand[2]), countArr);
        else if(strcmp(arrCommand[0], SHOW_MAIN) == 0)
            show_main(mainMemory, atoi(arrCommand[1]), countArr);
        else if(strcmp(arrCommand[0], SHOW_DISK) == 0)
            show_disk(virtualMemory, atoi(arrCommand[1]), countArr);
        else if(strcmp(arrCommand[0], SHOW_PTABLE) == 0)
            show_ptable(tableContents, countArr);
        else if(strcmp(arrCommand[0], QUIT) == 0)
            isQuit = 1;
        else
            fprintf(stdout, "Invalid command!\n");
    }
    return 0;
}
