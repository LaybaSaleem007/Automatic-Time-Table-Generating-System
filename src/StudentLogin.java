package TimeTable;

public class StudentLogin extends User
{
    public StudentLogin(int id,String name,String username,String password)
    {
        super(id,name,username,password);
    }

    @Override
    public void ShowMenu() {
        System.out.println("View TimeTable");

    }
}
