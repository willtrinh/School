/*
    Will Trinh
    ICS 53 Lab 3
*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct {
    unsigned char vpage;
    unsigned char valid;
    unsigned char dirty;
    unsigned char page;
} PageTableEntry;

static int mem[8] = {-1, -1, -1, -1, -1, -1, -1, -1};
static int disk[16] = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
static PageTableEntry PageTable[8] = {
        { 0x00, 0, 0, 0x00 },
        { 0x01, 0, 0, 0x01 },
        { 0x02, 0, 0, 0x02 },
        { 0x03, 0, 0, 0x03 },
        { 0x04, 0, 0, 0x04 },
        { 0x05, 0, 0, 0x05 },
        { 0x06, 0, 0, 0x06 },
        { 0x07, 0, 0, 0x07 },
};

//void Convert(int v_addr)
//{
//    int vpage = v_addr / 2;
//    int vpage_entry = PageTable[vpage];
//    int ppage = PageTable->page;
//    int p_addr = ppage * 2 + (v_addr % 2);
//}

void PrintPageTable()
{
    int i = 0;
    printf("VPageNum \tValid \t\tDirty \t\tPN\n");
    for (i = 0; i < 8; i++)
    {
        printf("%d \t\t%d \t\t%d \t\t%d\n", i, PageTable->valid, PageTable->dirty, i);
    }
}
int main()
{

    char userInput[1024];
    int address, num;
    char *token;
    int i = 0, j = 0;
    printf("$ ");

    while (fgets(userInput, 1024, stdin))
    {
        if (strncmp(userInput, "write", 5) == 0)
        {
            token = strtok(userInput, "write ");
            address = atoi(token);
            token = strtok(NULL, " ");
            num = atoi(token);
            mem[address] = num;
        }

        if (strncmp(userInput, "read", 4) == 0)
        {
            token = strtok(userInput, "read ");
            address = atoi(token);

            printf("%d\n", mem[address]);
        }

        if (strncmp(userInput, "showmain", 8) == 0)
        {
            token = strtok(userInput, "showmain ");
            int ppn = atoi(token);
            int address = ppn * 2;

            printf("Address     Contents\n");
            printf("%-12d%d\n", address, mem[address]);
            address++;
            printf("%-12d%d\n", address, mem[address]);

        }

        if (strncmp(userInput, "showdisk", 8) == 0)
        {
            token = strtok(userInput, "showdisk ");
            int dpn = atoi(token);
            int address = dpn * 2;
            printf("Address     Contents\n");
            printf("%-12d%d\n", address, disk[address]);
            address++;
            printf("%-12d%d\n", address, disk[address]);
        }

        if (strncmp(userInput, "showptable", 10) == 0)
        {
            PrintPageTable();
        }

        if (strncmp(userInput, "quit", 4) == 0)
        {
            exit(0);
        }
        printf("$ ");
    }

    return 0;
}
