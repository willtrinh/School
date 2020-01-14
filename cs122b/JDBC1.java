// JDBC Example - printing a database's metadata

import java.sql.*;

public class JDBC1
{
	
       public static void main(String[] arg) throws Exception
       {

               // Incorporate mySQL driver
               Class.forName("com.mysql.jdbc.Driver").newInstance();

                // Connect to the test database
               Connection connection = DriverManager.getConnection("jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false","mytestuser", "mypassword");

               // Create an execute an SQL statement to select all of table"Stars" records
               Statement select = connection.createStatement();
               ResultSet result = select.executeQuery("Select * from stars");

               // Get metatdata from stars; print # of attributes in table
               System.out.println("The results of the query");
               ResultSetMetaData metadata = result.getMetaData();
               System.out.println("There are " + metadata.getColumnCount() + " columns");

               // Print type of each attribute
               for (int i = 1; i <= metadata.getColumnCount(); i++)
                       System.out.println("Type of column "+ i + " is " + metadata.getColumnTypeName(i));

               // print table's contents, field by field
               while (result.next())
               {
                       System.out.println("Id = " + result.getString("ID"));
                       System.out.println("Name = " + result.getString("NAME"));
                       System.out.println("birthYear = " + result.getInt("BIRTHYEAR"));
                       System.out.println();
               }
       }
}
