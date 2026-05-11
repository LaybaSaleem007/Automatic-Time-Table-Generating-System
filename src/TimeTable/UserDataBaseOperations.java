package TimeTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDataBaseOperations
{
    public static User login(String username,String password)
    {
        Connection conn = DBConnection.getConnection();
  try
    {
     String query = "SELECT * FROM users WHERE username=? AND password=?";
      PreparedStatement ps = conn.prepareStatement(query);
       ps.setString(1,username);
      ps.setString(2,password);
      ResultSet rs = ps.executeQuery();
if(rs.next())
   {
       int id = rs.getInt("id");
       String name = rs.getString("name");
       String role = rs.getString("role");
         if(role.equalsIgnoreCase("ADMIN"))
         {
             return new AdminLogin(id,name,username,password);
         }
       else if(role.equalsIgnoreCase("TEACHER"))
        {
            return new TeacherLogin(id,name,username,password);
          }
        else if(role.equalsIgnoreCase("STUDENT"))
         {
           return new StudentLogin(id,name,username,password);
         }
            }
        }
  catch(Exception e)
        {
            e.printStackTrace();
        }
  return null;
    }
}