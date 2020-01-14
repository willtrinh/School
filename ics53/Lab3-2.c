/*
 * Name: Son Le
 * Student ID: 93351358
 * Partner: Will Trinh
 *
 * Lab #3
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

int oldestAccess [M_PAGES];

void showTable(int main[])
{
    int i;
    printf("MAIN: ");
    for(i=0; i<M_PAGES*2; i++)
    {
        printf("[%i]=%-5i", i, main[i]);
    }
    printf("\n");

    printf("QUEUE:");
    for(i=0; i<M_PAGES; i++)
    {
        printf("[%i]=%i    ", i, oldestAccess[i]);
    }
    printf("\n");
}

/******************************************
 * Find the less of uses
 ******************************************/
int getOldest()
{
    return oldestAccess[0];
}

/******************************************
 * Update the count of uses
 ******************************************/
void updateCountMemory(int page)
{
    int idx=0;

    while(idx<M_PAGES && oldestAccess[idx] != page)
        idx++;

    int i = idx;
    while(i<M_PAGES-1)
    {
        oldestAccess[i] = oldestAccess[i+1];
        i++;
    }

    oldestAccess[M_PAGES-1] = page;
}

/******************************************
 * Reset the memory
 ******************************************/
void initMemory(int table[], int max)
{
    int i;
    for(i=0; i<max*2; i++)
        table[i] = -1;
}
void initOldestAccess()
{
    int i;
    for(i=0;i<M_PAGES;i++)
        oldestAccess[i]=i;
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
 * Get value on disk and update
 ******************************************/
int getValue(int main[], int m_page, int disk[], int d_idx )
{
    return disk[d_idx];
}

/******************************************
 * Mod
 ******************************************/
void getMainMemoryReadyForNewData(int content[][4], int main[], int disk[], int curMainPage)
{
    // find holding current the longest age of main memory and update pTable
    int i=0;
    while(i<VM_PAGES && (content[i][1] != 1 || content[i][3] != curMainPage))
        i++;

    if(i<VM_PAGES)
    {
        if(content[i][2] == 1)
        {
            disk[i*2]   = main[curMainPage*2];
            disk[i*2+1] = main[curMainPage*2+1];
        }
        content[i][1] = 0;
        content[i][2] = 0;
        content[i][3] = i;
    }
}

/******************************************
 * Read function
 ******************************************/
void read(int content[][4], int main[], int disk[], int idx, int count)
{
    if(count == 2)
    {
        int page = idx/2;
        int offset = idx%2;

        if(content[page][1] == 0)
        {
            int curMainPage = getOldest();
            getMainMemoryReadyForNewData(content, main, disk, curMainPage);

            // copy from disk to main memory
            int curMainPageIndex = curMainPage*2;
            int curPageIndex = page*2;
            main[curMainPageIndex]     = disk[curPageIndex];
            main[curMainPageIndex+1]   = disk[curPageIndex+1];
            content[page][1] = 1;
            content[page][3] = curMainPage;


        }

        // print out data of the address
        int index = content[page][3]*2 + offset;
        fprintf(stdout, "value@address %i: %i\n", idx, main[index]);
        // update the current page of main memory
        updateCountMemory(index/2);
    }
    else
        fprintf(stdout, "Invalid command! The command takes one argument.\n");
}

/******************************************
 * Write function
 ******************************************/
void write(int content[][4], int main[], int disk[], int idx, int value, int count)
{
    if(count != 3)
        fprintf(stdout, "Invalid command! The command takes two arguments.\n");
    else if(value < 1)
        fprintf(stdout, "Invalid data! The value must be a positive integer.\n");
    else
    {
        int page = idx/2;
        int offset = idx%2;
        int curMainPage = 0;

        if(content[page][1] == 0)
        {
            curMainPage = getOldest();
            getMainMemoryReadyForNewData(content, main, disk, curMainPage);

            content[page][1] = 1;
            content[page][3] = curMainPage;
        }
        else
        {
            curMainPage = content[page][3];
        }

        // store new data into main memory
        if(content[page][2] == 0)
        {
            if(offset == 0)
                main[curMainPage*2+1] = getValue(main, curMainPage, disk, idx+1);
            else
                main[curMainPage*2] = getValue(main, curMainPage, disk, idx-1);
        }

        content[page][2] = 1;
        main[curMainPage*2+offset] = value;

        // update the current page of main memory
        updateCountMemory(curMainPage);

        /*
        int page = idx/2;
        int offset = idx%2;
        int curMainPage = 0;

        if(disk[idx] == -1)
        {
            // update to disk
            disk[idx] = value;

            if(content[page][1] == 1)
            {
                curMainPage = content[page][3];
            }
            else
            {
                curMainPage = getOldest();

                // find holding current the longest age of main memory and update pTable
                int i=0;
                while(i<VM_PAGES && (content[i][1] != 1 || content[i][3] != curMainPage))
                    i++;

                if(i<VM_PAGES)
                {
                    content[i][1] = 0;
                    content[i][3] = i;
                }

                content[page][1] = 1;
                content[page][3] = curMainPage;
            }

            // load data to main memory
            main[curMainPage*2] = disk[page*2];
            main[curMainPage*2+1] = disk[page*2+1];
        }
        else
        {
            curMainPage = getOldest();
            printf("####%i \n", curMainPage);
            // find holding current the longest age of main memory and update pTable
            int i=0;
            while(i<VM_PAGES && (content[i][1] != 1 || content[i][3] != curMainPage))
                i++;

            if(i<VM_PAGES)
            {
                content[i][1] = 0;
                content[i][3] = i;
            }

            content[page][1] = 1;
            content[page][2] = 1;
            content[page][3] = curMainPage;

            // store new value to main memory
            main[curMainPage*2 + offset] = value;
            if(offset == 0)
                main[curMainPage*2 + 1] = disk[idx+1];
            else
                main[curMainPage*2] = disk[idx-1];
        }

        // update the current page of main memory
        updateCountMemory(curMainPage);
        */
    }
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
        fprintf(stdout, "%-9i %-9i\n", page*2, table[page*2]);
        fprintf(stdout, "%-9i %-9i\n", page*2+1,table[page*2+1]);
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
        fprintf(stdout, "%-9i %-9i\n", page*2, table[page*2]);
        fprintf(stdout, "%-9i %-9i\n", page*2+1,table[page*2+1]);
    }

}

/******************************************
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
    int countArr;
    int isQuit = 0;
    int virtualMemory[VM_PAGES*2];
    int mainMemory[M_PAGES*2];
    int tableContents[VM_PAGES][4];

    initMemory(virtualMemory, VM_PAGES);
    initMemory(mainMemory, M_PAGES);
    initOldestAccess();
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
            read(tableContents, mainMemory, virtualMemory, atoi(arrCommand[1]), countArr);
        else if(strcmp(arrCommand[0], WRITE) == 0)
            write(tableContents, mainMemory, virtualMemory, atoi(arrCommand[1]), atoi(arrCommand[2]), countArr);
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
