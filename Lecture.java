package TimeTable;

public class Lecture
{
    public Subject subject;
    public Teacher teacher;
    public Room room;
    public String Day;
    public String TimeSlot;
        public Lecture(Subject subject,Teacher teacher,Room room,String Day,String TimeSlot)
        {
            this.subject=subject;
            this.teacher=teacher;
            this.room=room;
            this.Day=Day;
            this.TimeSlot=TimeSlot;
        }
}
