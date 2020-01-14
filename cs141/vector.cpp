/*
 * Name: Will Trinh
 * CS 141 - Programming Languages
 * Professor Klefstad
 * Homework #3
 * October 25, 2018
 */

#include <iostream>
#include <string.h>
#include <stdlib.h>
#include <iterator>
#include <vector>

using namespace std;
template
        <typename T>
class Vector
{
private:
    int sz; // the number of elements in this Vector
    T * buf; // the base of the array of Ts, you must allocate it
public:
    Vector( int n ) // Vector v1(10); -- create a 10 element Vector
    {
        sz = n;
        buf = new T[sz];
        for (int i = 0; i < sz; i++)
            buf[i] = 0;
    }

    Vector(initializer_list<T> L ) // Vector v1{T1, T2, T3};
    {
        sz = L.size();
        buf = new T[sz];

        int idx = 0;

        for (auto& element : L)
        {
            buf[idx] = element;
            idx++;

        }
    }

    // Destructor
    ~Vector() // destructor called automatically when a Vector dies
    {
        delete buf;
        buf = NULL;
    }

    // Copy constructor
    Vector( const Vector & v ) // Vector v2(v1);
    {
        sz = v.sz;
        buf = new T[sz];

        for (int i = 0; i < sz; i++)
            buf[i] = v.buf[i];
    }

    int size ( ) // v1.size() returns 10 for v1 example above
    {
        return sz;
    }

    T & operator [] ( const int i ) // T x = V[i];
    {
        if (i < 0 || i > sz) {
            throw "Error: Access Index Out of Bound Exception!";
        }
        return buf[i];
    }

    T operator * ( const Vector & v ) const // T x = V1 * V2
    {
        int size = 0;
        // If the vectors have different sizes, take the smaller size
        if (sz < v.sz)
            size = sz;
        else
            size = v.sz;

        T productSum = 0;
        for (int i = 0; i < size; i++)
        {
            productSum += buf[i] * v.buf[i];
        }
        return productSum;
    }

    Vector operator + ( const Vector & v ) const // V3 = V1 + V2;
    {
        int size = 0;
        if (sz < v.sz)
            size = v.sz;
        else
            size = sz;

        Vector<T> vectorSum = Vector(size);
        Vector<T> vector1 = Vector(size);
        Vector<T> vector2 = Vector(size);

        for (int i = 0; i < sz; i++)
            vector1[i] = buf[i];

        for (int i = 0; i < v.sz; i++)
            vector2[i] = v.buf[i];

        for (int i = 0; i < size; i++)
        {
            vectorSum[i] = vector1[i] + vector2[i];
        }

        return vectorSum;
    }

    const Vector & operator = ( const Vector & v ) // V1 = V2;
    {
        sz = v.sz;
        buf = v.buf;
    }
    bool operator == ( const Vector & v ) const //if ( V1 == V2 )...
    {
        if (sz != v.sz)
            return false;

        for (int i = 0; i < sz; i++)
        {
            if (buf[i] != v.buf[i])
                return false;
        }
        return true;
    }

    bool operator != ( const Vector & v ) const // if ( V1 != V2 )...
    {
        if (sz != v.sz)
            return true;

        for (int i = 0; i < sz; i++)
        {
            if (buf[i] != v.buf[i])
                return true;
        }
        return false;
    }
    friend Vector operator * ( const int n, const Vector & v )
// V1 = 20 * V2; -- each element of V1 will be element of V2 * 20
    {
        for (int i = 0; i < v.sz; i++)
        {
            v.buf[i] *= n;
        }
        return v;
    }

    friend Vector operator + ( const int n, const Vector & v )
// V1 = 20 + V2; -- each element of V1 will be element of V2 + 20
    {
        for (int i = 0; i < v.sz; i++)
        {
            v.buf[i] += n;
        }
        return v;
    }
    friend ostream& operator << ( ostream & o, const Vector & v )
// cout << V2; -- prints the vector in format (v0, v1, v2,...,vn)
    {
        o <<  "(";
        for (int i = 0; i < v.sz - 1; i++)
            o << v.buf[i] << ", ";
        o << v.buf[v.sz - 1] << ")";
    }
};

int main() // I’ll start it for you
{
    cout << "******************** Testing Initializer List ********************" << endl;
    Vector<int> intVec{1,3,5,7,9};
    cout << "intVec" << intVec << endl;
    Vector<double> doubleVec{1.5,2.5,3.5,4.5};
    cout << "doubleVec" << doubleVec << endl << endl;


    cout << "******************** Testing Copy Constructor ********************" << endl;
    Vector<int> iv(intVec);
    Vector<double> dv(doubleVec);
    cout << "iv" << iv << endl;
    cout << "dv" << dv << endl << endl;


    cout << "******************** Testing Vector Size ********************" << endl;
    cout << "intVec Size: " << iv.size() << endl;
    cout << "doubleVec Size: " << dv.size() << endl << endl;


    cout << "******************** Testing T& Operator[] ********************" << endl;
    cout << "Testing valid index iv[3]: " << iv.operator[](3) << endl;
    cout << "Testing invalid index iv[10]: ";
    try {
        iv.operator[](10);
    } catch (const char* x) {
        cout << x << endl << endl;
    }


    cout << "******************** Testing T Operator* ********************" << endl;
    Vector<int> iv1{1, 2, 3};
    cout << "Vectors: " << iv << iv1 << endl;
    cout << "Product Sum of two vectors: " << iv.operator*(iv1) << endl << endl;


    cout << "******************** Testing Vector Operator+ ********************" << endl;
    cout << "Vectors: " << iv << iv1 << endl;
    cout << "Vector Sum of two vectors: " << iv1.operator+(iv) << endl << endl;


    cout << "******************** Testing Vector Operator= ********************" << endl;
    Vector<double> dv1{1.1, 2.2, 3.3};
    cout << "Vectors: " << dv << dv1 << endl;
    cout << "Testing dv.operator=(dv1): " << dv.operator=(dv1) << endl;
    cout << "New Vectors dv & dv1: " << dv << dv1 << endl << endl;


    cout << "******************** Testing Vector Operator== ********************" << endl;
    Vector<double> dv2{4.1, 2.2, 3.3, 4.4};
    cout << "Vectors: " << dv1 << dv2 << endl;
    cout << "Testing dv1.operator==(dv2): " << boolalpha << dv1.operator==(dv2) << endl;
    cout << "Testing dv1.operator==(dv1): " << boolalpha << dv1.operator==(dv1) << endl << endl;


    cout << "******************** Testing Vector Operator!= ********************" << endl;
    cout << "Testing dv1.operator!=(dv2): " << boolalpha << dv1.operator!=(dv2) << endl;
    cout << "Testing dv1.operator!=(dv1): " << boolalpha << dv1.operator!=(dv1) << endl << endl;


    cout << "******************** Testing friend Vector Operator* ********************" << endl;
    cout << "Vector:" << iv << endl;
    Vector <int>iv2 = 20 * iv;
    cout << "Testing iv2 = 20 * iv: " << iv2 << endl << endl;


    cout << "******************** Testing friend Vector Operator+ ********************" << endl;
    cout << "Vector" << iv2 << endl;
    Vector<int>iv3 = 20 + iv2;
    cout << "Testing iv3 = 20 + iv2: " << iv2 << endl << endl;


    return 0;
}
