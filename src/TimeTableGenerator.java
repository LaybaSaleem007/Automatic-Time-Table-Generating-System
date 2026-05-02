
import java.util.*;
public class TimeTableGenerator {
    public static void generate(List<Subject> subjects,String[] days,String[] slots)
    {
        Random random=new Random();
            for(String day:days)
            {
                System.out.println("\n" +day+":");
                for(String slot:slots)
                {
                    Subject subject=subjects.get(random.nextInt(subjects.size()));
                    System.out.println(slot+"-->"+subject.name+"("+subject.Teacher+")");
                }
            }
    }
}
