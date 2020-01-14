/*
 *  Will Trinh
 *  ICS 53 - Lab 1
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_CHAR 100
#define MAX_PREREQ 5
#define MAX_COURSE 20
#define MAX_YEAR 10

struct course
{
    char *courseName;
    int countPreq;
    int quarterOffer;
    struct course *preReq[MAX_PREREQ];
};

struct course *courses[MAX_COURSE];

struct quarter
{
    int numOfCourse;
    struct course *courses[MAX_COURSE];
};
struct year
{
    struct quarter quarter[3];
};

struct schedule
{
    int numOfYear;
    struct year years[MAX_YEAR];
};

struct yearQuarter
{
    int year;
    int quarter;
};

int numOfCourses = 0;

int findCourse(char *courseName)
{
    int i = 0;
    for (i = 0; i < numOfCourses; i++)
    {
        if (strcmp(courses[i]->courseName, courseName) == 0)
            return i;
    }
    return -1;
}

void printSchedule(struct schedule *sch)
{
    int year = 0;
    int numYear = sch->numOfYear;
    for (year = 0; year <= numYear; year++)
    {
        int quarter = 0;
        for (quarter = 0; quarter < 3; quarter++)
        {
            if ((sch->years[year].quarter[quarter].numOfCourse) > 0)
            {
                int i = 0;
                printf("%d %d", year + 1, quarter + 1);
                while (sch->years[year].quarter[quarter].numOfCourse > i)
                {
                    printf(" %s", sch->years[year].quarter[quarter].courses[i]->courseName);
                    i++;
                }
                printf("\n");
            }
        }
    }
}

struct yearQuarter isInSchedule(struct course *course, struct schedule *sch)
{
    struct yearQuarter find;
    find.quarter = -1;
    find.year = -1;

    int year = 0;
    int numYear = sch->numOfYear;

    for (year = 0; year <= numYear; year++)
    {
        int quarter = 0;
        for (quarter = 0; quarter < 3; quarter++)
        {
            int i = 0;
            while (sch->years[year].quarter[quarter].numOfCourse > i)
            {
                if (sch->years[year].quarter[quarter].courses[i] == course)
                {
                    find.quarter = quarter;
                    find.year = year;
                    return find;
                }
                i++;
            }
        }
    }
    return find;
}

struct yearQuarter fillCourseToSchedule(struct course *course, struct schedule *sch)
{
    struct yearQuarter lastYearQuarter;
    lastYearQuarter.quarter = -1;
    lastYearQuarter.year = -1;

    //lastYearQuarter to see if course in already in schedule
    lastYearQuarter = isInSchedule(course, sch);
    if (lastYearQuarter.year != -1)
    {
        return lastYearQuarter;
    };

    //If course is not already in schedule, fill its prerequisite

    int i = 0;
    int preCourse = (course->countPreq);
    for (i = 0; i < preCourse; i++)
    {
        struct yearQuarter temp = fillCourseToSchedule(course->preReq[i], sch);
        if (temp.year > lastYearQuarter.year)
        {
            lastYearQuarter = temp;
        }
        else if (temp.year == lastYearQuarter.year && temp.quarter > lastYearQuarter.quarter)
        {
            lastYearQuarter.quarter = temp.quarter;
        }
    }

    //Filling in course
    if (lastYearQuarter.year == -1)
    {
        lastYearQuarter.year = 0;
    }
    else if ((course->quarterOffer - 1) <= lastYearQuarter.quarter)
        lastYearQuarter.year++;

    lastYearQuarter.quarter = course->quarterOffer - 1;
    i = sch->years[lastYearQuarter.year].quarter[lastYearQuarter.quarter].numOfCourse;
    sch->years[lastYearQuarter.year].quarter[lastYearQuarter.quarter].courses[i] = course;
    sch->years[lastYearQuarter.year].quarter[lastYearQuarter.quarter].numOfCourse++;

    if (sch->numOfYear < lastYearQuarter.year)
        sch->numOfYear = lastYearQuarter.year;
    return lastYearQuarter;
}


int main (int argc, char **argv)
{
    FILE *f1, *f2, *f3;

    f1 = fopen(argv[1], "r");
    f2 = fopen(argv[2], "r");
    f3 = fopen(argv[3], "r");

    /* Reading files into array of strings */
    //Processing Offering File into Array

    char temp[1000];
    char *oLine[MAX_CHAR];
    int countOffering = 0;

    while (fgets(temp, sizeof(temp), f1) != 0)
    {
        temp[strcspn(temp, "\r\n")] = '\0';
        oLine[countOffering] = strdup(temp);
        countOffering++;
    }
    fclose(f1);

    //Processing Prerequisite File into Array
    char *pLine[MAX_CHAR];
    int countPrereq = 0;

    while (fgets(temp, sizeof(temp), f2) != 0)
    {
        temp[strcspn(temp, "\r\n")] = '\0';
        pLine[countPrereq] = strdup(temp);
        countPrereq++;
    }
    fclose(f2);

    //Processing Requirement File into Array
    char *rLine[MAX_CHAR];
    int countReq = 0;

    while (fgets(temp, sizeof(temp), f3) != 0)
    {
        temp[strcspn(temp, "\r\n")] = '\0';
        rLine[countReq] = strdup(temp);
        countReq++;
    }
    fclose(f3);

    //Tokenize Offering Array
    int i = 0;
    for (i = 0; i < countOffering; i++)
    {
        courses[i] = malloc(sizeof(struct course));
        courses[i]->quarterOffer = -1;
        courses[i]->countPreq = 0;
        courses[i]->courseName = strtok(oLine[i], " ");
        if (courses[i]->courseName != NULL)
        {
            courses[i]->quarterOffer = (int) strtod(strtok(NULL, " "), NULL);
        }
        numOfCourses++;
    }

    //Tokenize Prereq Array
    for (i = 0; i < countPrereq; i++)
    {
        char *courseName = strtok(pLine[i], " ");
        if (courseName != NULL)
        {
            int cIndex = findCourse(courseName);
            if (cIndex != -1)
            {
                struct course *current = courses[cIndex];
                char *preReq = strtok(NULL, " ");
                while (preReq != NULL)
                {
                    int preIndex = findCourse(preReq);
                    if (preIndex > -1)
                    {
                        int index = current->countPreq;
                        current->preReq[index] = courses[preIndex];
                        current->countPreq++;
                    }
                    preReq = strtok(NULL, " ");
                }
            }
        }

    }

    //Tokenize Requirement Array
    char *req[MAX_COURSE];
    int reqCount = 0;
    req[reqCount] = strtok(rLine[0], " ");

    while (req[reqCount] != NULL)
    {
        req[++reqCount] = strtok(NULL, " ");
    }

    //Create a new schedule and initialize its members
    struct schedule studentSchedule = {0};
    struct schedule *scheduleptr = &studentSchedule;

    //Populate schedule
    for (i = 0; i < reqCount; i++)
    {
        int index = findCourse(req[i]);
        if (index > -1)
        {
            struct course *course = courses[index];
            if (course != NULL)
                fillCourseToSchedule(course, scheduleptr);
        }
    }

    //Print schedule
    printSchedule(scheduleptr);

    return 0;
}


