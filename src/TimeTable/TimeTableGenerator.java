package TimeTable;
import logic.ConflictChecker;

import java.util.*;
public class TimeTableGenerator {
    private List<Lecture> schedualedLectures = new ArrayList<>();

    public List<Lecture> generate(List<Subject> subjects, List<Teacher> teachers, List<Room> rooms, String[] days, String[] Slots) {
        Random random = new Random();
        for (String Day : days) {
            for (String Slot : Slots) {
                boolean placed = false;
                    Subject subject = subjects.get(random.nextInt(subjects.size()));
                Teacher teacher = subject.teacher;
                Room room = rooms.get(random.nextInt(rooms.size()));
                    Lecture lecture = new Lecture(subject, teacher, room, Day, Slot);
                    schedualedLectures.add(lecture);
                }
            }
        return schedualedLectures;
    }







































}

