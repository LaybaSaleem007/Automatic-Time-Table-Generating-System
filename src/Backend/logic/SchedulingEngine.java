package Backend.logic;

import Backend.logic.Model.*;
import TimeTable.TimetableDatabase;

import java.util.*;
import java.util.stream.Collectors;

public class SchedulingEngine {
    private TimetableDatabase database;
    private ConflictResolver conflictResolver;
    private RoomAllocator roomAllocator;
    private WorkloadBalancer workloadBalancer;
    private List<TimetableEntry> generatedTimetable;
    private Random random;

    public SchedulingEngine(TimetableDatabase database) {
        this.database = database;
        this.conflictResolver = new ConflictResolver();
        this.roomAllocator = new RoomAllocator();
        this.workloadBalancer = new WorkloadBalancer();
        this.generatedTimetable = new ArrayList<>();
        this.random = new Random();
    }

    public List<TimetableEntry> generateTimetable() {
        System.out.println("Starting timetable generation...");
        generatedTimetable.clear();

        List<Subject> allSubjects = database.getAllSubjects();
        List<Teacher> allTeachers = database.getAllTeachers();
        List<Room> allRooms = database.getAllRooms();
        List<String> sections = Arrays.asList("A", "B", "C", "D");

        if (allSubjects.isEmpty()) {
            System.out.println("❌ No subjects found! Please add subjects first.");
            return generatedTimetable;
        }
        if (allTeachers.isEmpty()) {
            System.out.println("❌ No teachers found! Please add teachers first.");
            return generatedTimetable;
        }
        if (allRooms.isEmpty()) {
            System.out.println("❌ No rooms found! Please add rooms first.");
            return generatedTimetable;
        }

        System.out.println("Subjects: " + allSubjects.size());
        System.out.println("Teachers: " + allTeachers.size());
        System.out.println("Rooms: " + allRooms.size());

        List<TimeSlot> availableSlots = generateTimeSlots();
        int totalScheduled = 0;

        for (String section : sections) {
            for (Subject subject : allSubjects) {
                System.out.println("  Trying to schedule: " + subject.getSubjectCode() + " for section " + section);

                List<Teacher> qualifiedTeachers = allTeachers.stream()
                        .filter(t -> t.getQualifiedSubjects().contains(subject.getSubjectCode()))
                        .collect(Collectors.toList());

                if (qualifiedTeachers.isEmpty()) {
                    System.out.println("    ❌ No qualified teacher found");
                    continue;
                }

                List<Room> suitableRooms = allRooms.stream()
                        .filter(r -> r.getRoomType().equals(subject.getRoomType()))
                        .collect(Collectors.toList());

                if (suitableRooms.isEmpty()) {
                    System.out.println("    ❌ No suitable room found");
                    continue;
                }

                boolean scheduled = false;
                for (TimeSlot slot : availableSlots) {
                    if (scheduled) break;

                    for (Teacher teacher : qualifiedTeachers) {
                        if (scheduled) break;

                        if (!teacher.canTakeMoreHours()) continue;

                        for (Room room : suitableRooms) {
                            if (isSectionBusy(section, slot)) continue;
                            if (isTeacherBusy(teacher, slot)) continue;
                            if (isRoomBusy(room, slot)) continue;

                            if (!room.isSlotAvailable(slot)) continue;

                            System.out.println("    ✅ SCHEDULED: " + slot.getDay() + " " + slot.getTimeRange() +
                                    " | Teacher: " + teacher.getName() + " | Room: " + room.getRoomNumber());

                            TimetableEntry entry = new TimetableEntry(subject, teacher, room, slot, section, 30);
                            generatedTimetable.add(entry);
                            room.bookSlot(slot);
                            teacher.addAssignedHour();
                            scheduled = true;
                            totalScheduled++;
                            break;
                        }
                    }
                }

                if (!scheduled) {
                    System.out.println("    ❌ No available slot found");
                }
            }
        }

        System.out.println("\n✅ Timetable generation completed! Generated " + totalScheduled + " entries.");
        return generatedTimetable;
    }

    private List<TimeSlot> generateTimeSlots() {
        List<TimeSlot> slots = new ArrayList<>();
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        String[] timeRanges = {"9:00-10:00", "10:00-11:00", "11:00-12:00", "12:00-13:00", "14:00-15:00", "15:00-16:00"};

        for (String day : days) {
            for (String timeRange : timeRanges) {
                String[] times = timeRange.split("-");
                slots.add(new TimeSlot(day, times[0], times[1]));
            }
        }
        return slots;
    }

    private boolean isSectionBusy(String section, TimeSlot slot) {
        return generatedTimetable.stream().anyMatch(e ->
                e.getStudentSection().equals(section) &&
                        e.getTimeSlot().getDay().equals(slot.getDay()) &&
                        e.getTimeSlot().getTimeRange().equals(slot.getTimeRange()));
    }

    private boolean isTeacherBusy(Teacher teacher, TimeSlot slot) {
        return generatedTimetable.stream().anyMatch(e ->
                e.getTeacher().equals(teacher) &&
                        e.getTimeSlot().getDay().equals(slot.getDay()) &&
                        e.getTimeSlot().getTimeRange().equals(slot.getTimeRange()));
    }

    private boolean isRoomBusy(Room room, TimeSlot slot) {
        return generatedTimetable.stream().anyMatch(e ->
                e.getRoom().equals(room) &&
                        e.getTimeSlot().getDay().equals(slot.getDay()) &&
                        e.getTimeSlot().getTimeRange().equals(slot.getTimeRange()));
    }

    public void publishTimetable() {
        database.saveTimetable(generatedTimetable);
        NotificationService notificationService = new NotificationService(database);
        notificationService.notifyAllUsers("Timetable Published", "The new timetable has been published.", "SCHEDULE_CHANGE");
        System.out.println("Timetable published successfully!");
    }

    public List<TimetableEntry> getGeneratedTimetable() { return generatedTimetable; }
}