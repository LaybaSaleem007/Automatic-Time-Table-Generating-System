package TimeTable;

public abstract class User
{
    protected int id;
    protected String name;
    protected String username;
    protected String password;
        public User(int id,String name,String username,String password)
        {
            this.id=id;
            this.name=name;
            this.username=username;
            this.password=password;
        }
        public abstract void ShowMenu();
}
