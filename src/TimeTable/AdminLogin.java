package TimeTable;

public class AdminLogin extends User
{
    public AdminLogin(int id,String name,String username,String password)
    {
        super(id,name,username,password);
    }
    @Override
    public void ShowMenu() {
        System.out.println("1.Add Subject");
        System.out.println("2.Add teacher");
        System.out.println("3.add Room");
        System.out.println("4.Generate Time table");
    }
}
