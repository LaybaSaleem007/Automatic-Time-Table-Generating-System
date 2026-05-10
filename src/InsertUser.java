    package TimeTable;

    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.SQLException;

    public class InsertUser
    {
        public static void main(String[] args) {
            Connection conn=DBConnection.getConnection();
            try {
                String query = "INSERT INTO users(name,username,password,role) VALUES (?,?,?,?)";
                PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1,"Admin");
            ps.setString(2,"admin");
            ps.setString(3,"123");
            ps.setString(4,"ADMIN");
            ps.executeUpdate();
                System.out.println("User inserted successfuly");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
