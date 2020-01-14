/*
 * Name: Will Trinh
 * ID: 17986840
 * CS 141 - Programming Languages
 * Homework 2
 * October 18, 2018
 */
//package hw1;
#include <stdio.h>
#include <malloc.h>
#include <string.h>
#include <math.h>
#define PI 3.14159

typedef double (*VirtualMethodPointer)(void *);

typedef VirtualMethodPointer * VTableType;

/*****************************************
 *
 * Shape
 *
 * ***************************************
 */

typedef struct Shape
{
    VTableType VPointer;
    char *name;
    double area;
} Shape;

double Shape_area(Shape *_this)
{
    return _this->area;

}

void Shape_print(Shape *_this)
{
}

void Shape_draw(Shape *_this)
{
}
VirtualMethodPointer Shape_VTable [] = {
        (VirtualMethodPointer)Shape_print,
        (VirtualMethodPointer)Shape_area,
        (VirtualMethodPointer)Shape_draw
};

Shape *Shape_Shape(Shape *_this, char* newName)
{
    _this->VPointer = Shape_VTable;
    _this->name = newName;
    return _this;
}

/*****************************************
 *
 * Triangle
 *
 * ***************************************
 */
typedef struct Triangle
//extends Shape
{
    VTableType VPointer;
    char *name;
    int height;
    int base;
} Triangle;

double triangle_area(Triangle *_this)
{
    return (_this->height * _this->base) / 2.0;
}

void triangle_draw(Triangle *_this)
{
    for (int i = 0; i <= _this->height; i++)
    {
        for (int j = 0; j < i; j++)
        {
            printf("* ");
        }
        printf("\n");
    }
}

void triangle_print(Triangle *_this)
{
    printf("%s: height = %d, base = %d", _this->name, _this->height, _this->base);
}

VirtualMethodPointer Triangle_VTable [] = {
        (VirtualMethodPointer)triangle_print,
        (VirtualMethodPointer)triangle_area,
        (VirtualMethodPointer)triangle_draw
};

Triangle * Triangle_Triangle(Triangle * _this, char* newName, int newHeight, int newBase)
{
    Shape_Shape((Shape*)_this, newName);
    _this->VPointer = Triangle_VTable;
    _this->height = newHeight;
    _this->base = newBase;
    return _this;
}

/*****************************************
 *
 * Circle
 *
 * ***************************************
 */
typedef struct Circle
//extends Shape
{
    VTableType VPointer;
    char *name;
    int radius;
} Circle;

double circle_area(Circle *_this)
{
    return PI * _this->radius * _this->radius;
}

void circle_draw(Circle *_this)
{
    double distance;

    for (int i = 0; i <= _this->radius * 2; i++)
    {
        for (int j = 0; j <= _this->radius * 2; j++)
        {
            distance = sqrt((i - _this->radius) * (i - _this->radius)
                                 + (j - _this->radius) * (j - _this->radius));
            if (distance > _this->radius - 0.5 && distance < _this->radius + 0.5)
                printf("* ");
            else
                printf("  ");
        }
        printf("\n");
    }
}

void circle_print(Circle *_this)
{
    printf("%s: radius = %d", _this->name,_this->radius);
}

VirtualMethodPointer Circle_VTable [] = {
        (VirtualMethodPointer)circle_print,
        (VirtualMethodPointer)circle_area,
        (VirtualMethodPointer)circle_draw
};

Circle *Circle_Circle(Circle * _this, char* newName, int newRadius)
{
    Shape_Shape((Shape*)_this, newName);
    _this->VPointer = Circle_VTable;
    _this->radius = newRadius;
    return _this;
}
/*****************************************
 *
 * Square
 *
 * ***************************************
 */
typedef struct Square
//extends Shape
{
    VTableType VPointer;
    char *name;
    int side;

} Square;

double square_area(Square *_this)
{
    return _this->side * _this->side;
}

void square_draw(Square *_this)
{
    for (int i = 0; i <= _this->side; i++)
    {
        for (int j = 0; j <= _this->side; j++)
        {
            if (i == 0 || j == 0 || i == _this->side || j == _this->side)
               printf("*  ");
            else
                printf("   ");
        }
        printf("\n");
    }
}
void square_print(Square *_this)
{
    printf("%s: side = %d",_this->name, _this->side);
}

VirtualMethodPointer Square_VTable [] = {
        (VirtualMethodPointer)square_print,
        (VirtualMethodPointer)square_area,
        (VirtualMethodPointer)square_draw
};

Square *Square_Square(Square * _this, char *newName, int newSide)
{
    Shape_Shape((Shape*)_this, newName);
    _this->VPointer = Square_VTable;
    _this->side = newSide;
    return _this;
}

/*****************************************
 *
 * Rectangle
 *
 * ***************************************
 */
typedef struct Rectangle
//extends Square
{
    VTableType VPointer;
    char *name;
    int height;
    int width;
} Rectangle;

double rectangle_area(Rectangle *_this)
{
    return _this->height * _this->width;
}

void rectangle_draw(Rectangle *_this)
{
    for (int i = 0; i <= _this->height; i++) {
        for (int j = 0; j <= _this->width; j++) {
            if (i == 0 || j == 0 || i == _this->height || j == _this->width)
                printf("* ");
            else
                printf("  ");
        }
        printf("\n");
    }
}

void rectangle_print(Rectangle *_this)
{
    printf("%s: height = %d, width = %d", _this->name, _this->height, _this->width);
}

VirtualMethodPointer Rectangle_VTable [] = {
        (VirtualMethodPointer)rectangle_print,
        (VirtualMethodPointer)rectangle_area,
        (VirtualMethodPointer)rectangle_draw
};

Rectangle *Rectangle_Rectangle(Rectangle * _this, char* newName, int newHeight, int newWidth)
{
    Square_Square((Square*)_this, newName, newHeight);
    _this->VPointer = Rectangle_VTable;
    _this->height = newHeight;
    _this->width = newWidth;
    return _this;
}

/*****************************************
 *
 * Main
 *
 * ***************************************
 */
int main()
{
    Shape * p [] = {
            (Shape *) Triangle_Triangle((Triangle *) malloc(sizeof(Triangle)),
                                               "FirstTriangle", 5, 5),
            (Shape *) Triangle_Triangle((Triangle *) malloc(sizeof(Triangle)),
                                               "SecondTriangle", 4, 3),
            (Shape *) Circle_Circle((Circle *) malloc(sizeof(Circle)),
                                           "FirstCircle", 5),
            (Shape *) Circle_Circle((Circle *) malloc(sizeof(Circle)),
                                           "SecondCircle", 10),
            (Shape *) Square_Square((Square *) malloc(sizeof(Square)),
                                           "FirstSquare", 5),
            (Shape *) Square_Square((Square *) malloc(sizeof(Square)),
                                           "SecondSquare", 10),
            (Shape *) Rectangle_Rectangle((Rectangle *) malloc(sizeof(Rectangle)),
                                                 "FirstRectangle", 4, 8),
            (Shape *) Rectangle_Rectangle((Rectangle *) malloc(sizeof(Rectangle)),
                                                 "SecondRectangle", 8, 4)
    };

    // print shapes information
    for (int i = 0; i < sizeof(p)/sizeof(*p); i++)
    {
        printf("", (p[i]->VPointer[0])(p[i]));
        printf("\n");
        if (i % 2 != 0)
            printf("\n");
    }

    // print totalArea
    double totalArea = 0.0;
    for (int i = 0; i < sizeof(p)/sizeof(*p); i++)
    {
        totalArea += ("%f", (p[i]->VPointer[1])(p[i]));

    }
    printf("Total Area = %.3f", totalArea);
    printf("\n\n");

   	// draw shapes
    for (int i = 0; i < sizeof(p)/sizeof(*p); i++)
    {
        printf("%s\n", (p[i]->name));
        printf("\n", (p[i]->VPointer[2])(p[i]));
    }

    // free all heap blocks
    for (int i = 0; i < sizeof(p)/sizeof(*p); i++)
    	free (p[i]);

    return 0;
}

