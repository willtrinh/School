/********************************
 * Name: Son Le                 *
 * Student ID: 93351358         *
 * 4 March 2018                 *
 *                              *
 * Partner: Will Trinh          *
 *                              *
 * Lab #4                       *
 ********************************/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define HEAP_SIZE 127
#define COMMAND_LINE_LENGTH 256

// prints out information about all of the blocks
void blocklist(char *heap)
{
    int idx = 0;

    fprintf(stdout, "Size    Allocated    Start   End\n" );

    while(idx<HEAP_SIZE)
    {
        // print size
        fprintf(stdout, "%-8i", heap[idx+1]);

        //print isAllocated
        if(heap[idx]==0)
            fprintf(stdout, "%-13s", "no");
        else
            fprintf(stdout, "%-13s", "yes");

        // print start index, end index
        fprintf(stdout, "%-8i%-8i\n", idx, idx+heap[idx+1]-1);

        // increasing idx
        idx = idx+heap[idx+1];
    }
}
// find index of a block
int get_index_of_block(char *heap, int block)
{
    int idx=0;
    while(idx<HEAP_SIZE && heap[idx] != block)
        idx = idx + heap[idx+1];

    return idx;
}

// find an index to insert
int get_index_to_insert(char *heap, int length)
{
    int idx=0;

    while(idx < HEAP_SIZE && !(heap[idx] == 0 && heap[idx+1] >= (length+2)))
    {
        //if(heap[idx] == 0 && heap[idx+1] >= (length+2))
        //    break;
        idx = idx + heap[idx+1];
    }

    if(idx >= HEAP_SIZE)
        idx = -1;

    return idx;
}

// allocate a block of memory
void allocate(char *heap, int *curBlock, int length)
{
    int idx;
    idx = get_index_to_insert(heap, length);

    if(idx != -1)
    {
        // re-count new current block
        *curBlock = *curBlock +1;

        // insert
        int unallocated_length = heap[idx+1] - length - 2;
        if(unallocated_length>2)
        {
            heap[idx+length+2] = 0;
            heap[idx+length+3] = heap[idx+1] - length - 2;
            heap[idx+1] = length+2;
        }

        heap[idx] = *curBlock;


        // print out return value
        fprintf(stdout, "%i\n", *curBlock);
    }
    else
        fprintf(stdout, "Allocation Failed\n");
}

// free a block of memory
void freeMemory(char *heap, int block)
{
    int idx=0;
    idx = get_index_of_block(heap, block);
    if(idx != -1)
        heap[idx] = 0;
    else
        fprintf(stdout, "Invalid block!\n");
}

// prints the header of a block
void printheader(char *heap, int block)
{
    int idx;
    idx = get_index_of_block(heap, block);
    if(idx != -1)
        fprintf(stdout, "%02x%02x\n", heap[idx], (heap[idx+1]<<1)|1);
    else
        fprintf(stdout, "Invalid block!\n");
}

// writes characters into a block in the heap
void writeheap(char *heap, int block, char value, int copies)
{
    int idx, i;
    idx = get_index_of_block(heap, block);
    if(idx != -1)
    {
        if(copies < heap[idx+1]-1)
        {
            for(i=0; i<copies; i++)
            {
                heap[idx+2+i] = value;
            }
        }
        else
            fprintf(stdout, "ERR: Write too big!\n");
    }
    else
        fprintf(stdout, "Invalid block!\n");

}

// prints out the contents of a portion of the heap
void printheap(char *heap, int block, int copies)
{
    int idx, i;
    idx = get_index_of_block(heap, block);
    if(idx != -1)
    {
        for(i=0; i<copies && i<heap[idx+1]; i++)
        {
            if(heap[idx+2+i] != NULL)
                fprintf(stdout, "%c", heap[idx+2+i]);
        }
        fprintf(stdout, "\n");
    }
    else
        fprintf(stdout, "Invalid block!\n");
}

int main()
{
    // user-input variables
    char commandLine[COMMAND_LINE_LENGTH];
    char *arrCommand[COMMAND_LINE_LENGTH];
    int countArr;
    int isQuit = 0;

    // program variables
    char *heap = (char*) malloc(HEAP_SIZE);
    int curBlock = 0;

    //initial heap
    heap[0] = 0;
    heap[1] = HEAP_SIZE;


    /******************************************/
    while(isQuit == 0)
    {
        countArr = 0;
        /******************************************
         * Read a new command line
         ******************************************/
        printf(">");
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
        if(strcmp(arrCommand[0], "blocklist") == 0)
            blocklist(heap);
        else if(strcmp(arrCommand[0], "allocate") == 0)
            allocate(heap, &curBlock, atoi(arrCommand[1]));
        else if(strcmp(arrCommand[0], "free") == 0)
            freeMemory(heap, atoi(arrCommand[1]));
        else if(strcmp(arrCommand[0], "printheader") == 0)
            printheader(heap, atoi(arrCommand[1]));
        else if(strcmp(arrCommand[0], "printheap") == 0)
            printheap(heap, atoi(arrCommand[1]),atoi(arrCommand[2]));
        else if(strcmp(arrCommand[0], "writeheap") == 0)
            writeheap(heap, atoi(arrCommand[1]), *arrCommand[2], atoi(arrCommand[3]));
        else if(strcmp(arrCommand[0], "quit") == 0)
            isQuit = 1;
        else
            fprintf(stdout, "Invalid command!\n");
    }

    free(heap);

    return 0;
}
