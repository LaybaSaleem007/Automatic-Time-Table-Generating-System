package TimeTable;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection
{
    private static final String URL="jdbc:mysql://localhost:3306/TimeTable";
      private static final String USER="root";
       private static final String PASSWORD="LBCMZ1014@S";
       public static Connection getConnection()
       {
           try
           {
               Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
               System.out.println("connection succesful");
               return conn;
           }catch(Exception e)
           {
               System.out.println("Connection failed");
               e.printStackTrace();
               return null;

           }

       }

}
