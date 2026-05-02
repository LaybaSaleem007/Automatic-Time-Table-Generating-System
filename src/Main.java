
import java.util.*;
public class Main
{
    public static void main(String[] args) {
        List<Subject> subjects=new ArrayList<>();
           subjects.add(new Subject("Math","Sir Ali"));
           subjects.add(new Subject("Physics","Mam Shumaila"));
           subjects.add(new Subject("DB","Sir Mudassar"));
           subjects.add(new Subject("OOP-L","Sir Shahmeer"));
            String [] days={"Monday","Tuesday","Wednesday","Thursday","Friday"};
                String [] slots={"9-10","10-11","11-12","1-2","2-3","3-4"};
                TimeTableGenerator.generate(subjects,days,slots);
    }
}