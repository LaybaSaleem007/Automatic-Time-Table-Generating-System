import TimeTable.*;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Teacher t1 = new Teacher(1, "Ali");
        Teacher t2 = new Teacher(2, "Saira");
        Teacher t3 = new Teacher(3, "Shumaila");
        List<Teacher> teachers = Arrays.asList(t1, t2, t3);

        List<Subject> subjects = Arrays.asList(
                new Subject(1, "Multivariate Calculus", t2),
                new Subject(2, "Linear Algebra", t3),
                new Subject(3, "Database", t1),
                new Subject(4, "DLD", t1)
        );

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        List<Room> rooms = Arrays.asList(
                new Room(10, "R11"),
                new Room(11, "R12")
        );

        String[] slots = {"9-10", "10-11", "11-12", "1-2", "2-3", "3-4"};

        TimeTableGenerator generator = new TimeTableGenerator();

        List<Lecture> timetable = generator.generate(subjects, teachers, rooms, days, slots);

        System.out.println("Size = " + timetable.size());

        for (Lecture l : timetable) {
            System.out.println(l.Day + " " + l.TimeSlot + " --> " + l.subject.name + " | " + l.teacher.name);
        }
    }
}