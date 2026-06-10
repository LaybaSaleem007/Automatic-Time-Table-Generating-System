package TimeTable;

import java.sql.*;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Testing MySQL Connection...");

        String url = "jdbc:mysql://localhost:3306/TimeTableDB?useSSL=false";
        String user = "root";
        String password = "";  // CHANGE to your actual password

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ CONNECTION SUCCESSFUL!");

            // Test query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            if(rs.next()) {
                System.out.println("✅ Database query works!");
            }

            conn.close();
        } catch (Exception e) {
            System.out.println("❌ FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}