/*
    Will Trinh
    ICS 53
    Lab 1
*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define COURSE_NAME 20
#define MAX_CHAR 100
#define YEARS 10
#define MAX_PREREQ 5
#define MAX_COURSES 20
#define QUARTER 3

int countOffering = 0, countPrereq = 0, countReq = 0;
char offeringArray[MAX_COURSES][5][COURSE_NAME];
char prerequisiteArray[MAX_COURSES][MAX_PREREQ][COURSE_NAME];
char requirementArray[MAX_COURSES][MAX_CHAR];
char *schedule[YEARS][QUARTER][20];
int cntPlanYear = 0, cntPlan[YEARS][QUARTER];

//Check if course is in Course Plan
int inCoursePlan(char *course)
{
    int yr, qrt, idx;
    for (yr = 0; yr < YEARS; yr++)
    {
        for (qrt = 0; qrt < QUARTER; qrt++)
        {
            for (idx = 0; idx < MAX_COURSES; idx++)
            {
                if (schedule[yr][qrt][idx] != NULL)
                {
                    if (strcmp(schedule[yr][qrt][idx], course) == 0)
                        return 1;
                }
            }
        }
    }
    return 0;
}

//Update Quarter Count
int updateQuarter(int year, int quarter)
{
    int i = cntPlan[year][quarter];
    cntPlan[year][quarter] = i + 1;

    return i;
}

//Get Course Offerings according to quarter
int getQuarterOffering(char *course)
{
    int i, j;
    for (i = 0; i < MAX_COURSES; i++)
    {
        for (j = 0; j < 4; j++)
        {
            if (strcmp(offeringArray[i][j], course) == 0)
            {
                return j;
            }
        }
    }
    return -1;
}

int getPosition(char *course, int* pYear, int* pQuarter)
{
    int i, j, k;
    for (i = 0; i <= cntPlanYear; i++)
    {
        for (j = 0; j < QUARTER; j++)
        {
            for (k = 0; k < cntPlan[i][j]; k++)
            {
                if (schedule[i][j][k] != NULL)
                {
                    if (strcmp(schedule[i][j][k], course) == 0)
                    {
                        *pYear = i;
                        *pQuarter = j;
                        return i;
                    }
                }
            }
        }
    }
}

int addCourse(char *course, int idx)
{
    if (inCoursePlan(course) == 0)
    {
        if (idx == -1)
        {
            int pQuarter = getQuarterOffering(course);
            schedule[0][pQuarter][updateQuarter(0, pQuarter)] = course;
        }
        else
        {
            int pYear = 0, pQuarter = 0, tmpYear, tmpQuarter;
            int i = 1;
            while (prerequisiteArray[idx][i][0] != '0')
            {
                getPosition(prerequisiteArray[idx][i], &tmpYear, &tmpQuarter);
                pYear = (pYear > tmpYear) ? pYear : tmpYear;
                pQuarter = (pQuarter > tmpQuarter) ? pQuarter : tmpQuarter;
                i++;
            }
            int courseQuarter = getQuarterOffering(course);
            if (courseQuarter > pQuarter)
                schedule[pYear][courseQuarter][updateQuarter(pYear, courseQuarter)] = course;
            else
            {
                pYear++;
                if (pYear > cntPlanYear)
                    cntPlanYear = pYear;
                schedule[pYear][courseQuarter][updateQuarter(pYear, courseQuarter)] = course;
            }
        }
    }
}

void getPrerequisite(char *course)
{
    int idx = 0;
    while (strcmp(prerequisiteArray[idx][0], course) != 0 && idx < countPrereq)
        idx++;
    if (idx == countPrereq)
        addCourse(course, -1);
    else
    {
        int j = 1;
        while (prerequisiteArray[idx][j][0] != '0')
        {
            getPrerequisite(prerequisiteArray[idx][j]);
            j++;
        }
        addCourse(course, idx);
    }
}

//Reset Count for Course Plan
void resetCntPlan()
{
    int i, j;
    for (i = 0; i < YEARS; i++)
    {
        for (j = 0; j < QUARTER; j++)
            cntPlan[i][j] = 0;
    }
    cntPlanYear = 0;
}

int main(int argc, char **argv)
{
    int i, j;

	FILE *f1, *f2, *f3;                                     //Open information files
	f1 = fopen(argv[1], "r");
	f2 = fopen(argv[2], "r");
	f3 = fopen(argv[3], "r");

	int isWord = 0, length = 0, isCourseName = 1;           //Reading Offering File
	int ch;
	char word[COURSE_NAME];

	while ((ch = fgetc(f1)) != EOF)
    {
        if (ch == '\n' || ch == '\0')
        {
            if (isWord == 1)
            {
                isWord = 0;
                length = 0;
                isCourseName = 1;
                countOffering++;
            }
        }
        else if (isspace(ch))
        {
            if (isWord == 1)
            {
                word[length] = '\0';
                isWord = 0;
                length = 0;
            }
            isCourseName = 0;
        }
        else if (isCourseName == 1)
        {
            isWord = 1;
            word[length] = ch;
            length++;
        }
        else
        {
            isWord = 1;
            int quarter = ch-49;
            for (i = 0; i < 4; i++)
            {
                if (i == quarter)
                {
                    for (j = 0; j < COURSE_NAME; j++)
                        offeringArray[countOffering][quarter][j] = word[j];
                }
                else
                    offeringArray[countOffering][i][0] = '0';
            }
        }
    }
	fclose(f1);

                                                    //Reading Prereq File
    isWord = 0;
    int col = 0;
    length = 0;

    while ((ch = fgetc(f2)) != EOF)
    {
        if (ch == '\n' || ch == '\0')
        {
            if (isWord == 1)
            {
                prerequisiteArray[countPrereq][col][length] = '\0';
                prerequisiteArray[countPrereq][col + 1][0] = '0';
                countPrereq++;
                col = 0;
                length = 0;
                isWord = 0;
            }
        }
        else if (isspace(ch))
        {
            if (isWord == 1)
            {
                prerequisiteArray[countPrereq][col][length] = '\0';
                col++;
                length = 0;
                isWord = 0;
            }
        }
        else
        {
            isWord = 1;
            prerequisiteArray[countPrereq][col][length] = ch;
            length++;
        }
    }
	fclose(f2);
                                                           //Reading Requirement File
    isWord = 0;
    length = 0;

    while ((ch = fgetc(f3)) != EOF)
    {
        if (isspace(ch))
        {
            if (isWord == 1)
            {
                requirementArray[countReq][length] = '\0';
                isWord = 0;
                length = 0;
                countReq++;
            }
        }
        else
        {
            isWord = 1;
            requirementArray[countReq][length] = ch;
            length++;
        }
    }
	fclose(f3);

	resetCntPlan();

	for (i = 0; i < countReq; i++)
    {
        getPrerequisite(requirementArray[i]);
    }

    int year = 0, quarter = 0, index = 0;
    for (year = 0; year <= cntPlanYear; year++)
    {
        for (quarter = 0; quarter < QUARTER; quarter++)
        {
            if (cntPlan[year][quarter] > 0)
            {
                printf ("%d %d ", year + 1, quarter + 1);
                for (index = 0; index < 20; index++)
                {
                    if(schedule[year][quarter][index] != NULL)
                    {
                        printf("%s ", schedule[year][quarter][index]);
                    }
                }
                printf("\n");
            }
        }
    }
	return 0;
}

