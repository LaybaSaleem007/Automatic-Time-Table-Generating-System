package TimeTable;

public class TeacherLogin extends User
{
    public TeacherLogin(int id,String name,String username,String password)
    {
        super(id,name,username,password);
    }

    @Override
    public void ShowMenu() {
        System.out.println("View My Schedule");

    }
}
