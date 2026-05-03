package TimeTable;

public class Subject
{
    public int id;
    public String name;
    public Teacher teacher;
    public Subject(int id,String name,Teacher teacher)
    {
        this.id=id;
        this.name=name;
        this.teacher=teacher;
    }
}
