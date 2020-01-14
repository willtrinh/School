/*
 * Name: Will Trinh
 * ID: 17986840
 * CS 141 - Programming Languages
 * Homework 1
 * October 11, 2018
 */
//package hw1;

import java.util.ArrayList;
import java.util.List;

/*****************************************
 * 
 * Shape
 * 
 * ***************************************
 */
class Shape {
	String name;
	int radius, height, width, base;
	Shape (String newName)
	{
		name = newName;
	}
	
	double area()
	{
		return 0.0;
	}
	
	void draw()
	{
		System.out.println("Shape.draw() You should never see this.");
	}
	void print()
	{
		
	}
}

/*****************************************
 * 
 * Triangle
 * 
 * ***************************************
 */
class Triangle extends Shape
{
	int myHeight, myBase;
	Triangle(String name, int h, int b) 
	{
		super(name);
		myHeight = h;
		myBase = b;
	}
	
	double area()
	{
		return (myHeight * myBase) / 2.0; 
	}
	
	void draw()
	{
		for (int i = 0; i <= myHeight; i++)
		{
			for (int j = 0; j < i; j++)
			{
				System.out.print("* ");
			}
			System.out.println("");
		}
	}
	
	void print()
	{
		System.out.println(name + ": height = " + myHeight + ", base = " + myBase);
	}
}

/*****************************************
 * 
 * Circle
 * 
 * ***************************************
 */
class Circle extends Shape
{
	int radius;
	Circle(String name, int r) 
	{
		super(name);
		radius = r;
	}
	
	double area()
	{
		return Math.PI * Math.pow(radius, 2);
	}
	
	void draw()
	{
		//distance to center of circle
		double distance;
		
		for (int i = 0; i <= radius * 2; i++)
		{
			for (int j = 0; j <= radius * 2; j++)
			{
				//formula derived from wikipedia & online forum
				distance = Math.sqrt((i - radius) * (i - radius)
						+ (j - radius) * (j - radius));
				if (distance > radius - 0.5 && distance < radius + 0.5)
					System.out.print("* ");
				else
					System.out.print("  ");
			}
			System.out.println();
		}
	}
	
	void print()
	{
		System.out.println(name + ": radius = " + radius);
	}
}

/*****************************************
 * 
 * Square
 * 
 * ***************************************
 */
class Square extends Shape
{
	int side;
	Square(String name, int s)
	{
		super(name);
		side = s;
	}
	
	double area()
	{
		return side * side;
	}
	
	void draw()
	{
		for (int i = 0; i <= side; i++)
		{
			for (int j = 0; j <= side; j++)
			{
				if (i == 0 || j == 0 || i == side || j == side)
					System.out.print("*  ");
				else
					System.out.print("   ");
			}
			System.out.println();
		}
	}
	
	void print()
	{
		System.out.println(name + ": side = " + side);
	}
}

/*****************************************
 * 
 * Rectangle
 * 
 * ***************************************
 */
class Rectangle extends Square 
{
	int height, width;
	Rectangle(String name, int h, int w)
	{
		super(name, w);
		height = h;
		width = w;
	}
	
	double area()
	{
		return height * width;
	}
	
	void draw()
	{
		for (int i = 0; i <= height; i++)
		{
			for (int j = 0; j <= width; j++)
			{
				if (i == 0 || j == 0 || i == height || j == width)
					System.out.print("* ");
				else
					System.out.print("  ");
			}
			System.out.println();
		}
	}
	
	void print()
	{
		System.out.println(name + ": height = " + height + ", width = " + width);
	}
}

/*****************************************
 * 
 * Picture
 * 
 * ***************************************
 */
class Picture
{
	List<Shape> p = new ArrayList<Shape>();
	void add(Shape sh)
	{
		p.add(sh);
	}
	
	void printShapes()
	{
		int i = 0;
		for (Shape s : p)
		{
			s.print();
			i++;
			if (i % 2 == 0)
				System.out.println();
		}
	}
	
	void drawAll()
	{	
		for (Shape s : p)
		{
			System.out.println(s.name);
			s.draw();
			System.out.println();
		}
	}
	
	double totalArea()
	{
		double totalArea = 0.0;
		for (Shape s : p)
		{
			totalArea += s.area();
			
		}
		return totalArea;
	}
}

/*****************************************
 * 
 * Main
 * 
 * ***************************************
 */
public class mainClass
{
	static void println(double d)
	{
		System.out.printf("\nTotal Area is %.3f", d);
		System.out.println("\n");
	}
	public static void main(String[] args)
	{
		Picture p = new Picture();
		p.add(new Triangle("FirstTriangle", 5,5));
		p.add(new Triangle("SecondTriangle", 4,3));
		p.add(new Circle("FirstCircle", 5));
		p.add(new Circle("SecondCircle", 10));
		p.add(new Square("FirstSquare", 5));
		p.add(new Square("SecondSquare", 10));
		p.add(new Rectangle("FirstRectangle", 4,8));
		p.add(new Rectangle("SecondRectangle", 8,4));
		
		System.out.println("******************************************************\n");
		System.out.println("		 printShape()\n");
		System.out.println("******************************************************");
		p.printShapes();
		
		System.out.println("******************************************************");
		System.out.println("		 totalArea()\n");
		System.out.println("******************************************************");
		println(p.totalArea());
		
		System.out.println("******************************************************\n");
		System.out.println("		 drawAll()\n");
		System.out.println("******************************************************");
		p.drawAll();
	}
}
