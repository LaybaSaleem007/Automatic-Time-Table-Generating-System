package logic;

import Model.Subject;
import Model.Teacher;
import Model.TimeSlot;
import Model.*;
import TimeTable.TimetableDatabase;
import java.util.*;
import java.util.stream.Collectors;

public class SchedulingEngine {
    private TimeTable.TimetableDatabase database;  // FIX: Use fully qualified type
    private ConflictResolver conflictResolver;
    private RoomAllocator roomAllocator;
    private WorkloadBalancer workloadBalancer;
    private List<TimetableEntry> generatedTimetable;
    private Random random;

    public SchedulingEngine(TimeTable.TimetableDatabase database) {
        this.database = database;
        this.conflictResolver = new ConflictResolver();
        this.roomAllocator = new RoomAllocator();
        this.workloadBalancer = new WorkloadBalancer();
        this.generatedTimetable = new ArrayList<>();
        this.random = new Random();
    }

    // Main scheduling algorithm
    public List<TimetableEntry> generateTimetable() {
        System.out.println("Starting timetable generation...");
        generatedTimetable.clear();

        List<Subject> allSubjects = database.getAllSubjects();
        List<Teacher> allTeachers = database.getAllTeachers();
        List<Room> allRooms = database.getAllRooms();
        List<String> sections = Arrays.asList("A", "B", "C", "D");

        // Define available time slots
        List<TimeSlot> availableSlots = generateTimeSlots();

        // Group subjects by department and semester
        Map<String, List<Subject>> subjectsByDeptSem = allSubjects.stream()
                .collect(Collectors.groupingBy(s -> s.getDepartment() + "_" + s.getSemester()));

        // For each department and semester
        for (Map.Entry<String, List<Subject>> entry : subjectsByDeptSem.entrySet()) {
            String deptSem = entry.getKey();
            List<Subject> subjects = entry.getValue();

            for (String section : sections) {
                for (Subject subject : subjects) {
                    TimetableEntry entry_ = scheduleSubject(subject, section,
                            allTeachers, allRooms,
                            availableSlots);
                    if (entry_ != null) {
                        generatedTimetable.add(entry_);
                        // Mark the slot as booked
                        markSlotAsBooked(entry_.getTimeSlot(), entry_.getRoom(),
                                entry_.getTeacher(), entry_.getStudentSection());
                    }
                }
            }
        }

        generatedTimetable = conflictResolver.resolveAllConflicts(generatedTimetable, database);

        // Balance teacher workloads
        workloadBalancer.balanceWorkload(generatedTimetable, database);

        // Update student timetables
        updateStudentTimetables();

        System.out.println("Timetable generation completed. Generated " +
                generatedTimetable.size() + " entries.");

        return generatedTimetable;
    }

    private TimetableEntry scheduleSubject(Subject subject, String section,
                                           List<Teacher> teachers, List<Room> rooms,
                                           List<TimeSlot> availableSlots) {
        // Find qualified teachers
        List<Teacher> qualifiedTeachers = teachers.stream()
                .filter(t -> t.getQualifiedSubjects().contains(subject.getSubjectCode()))
                .collect(Collectors.toList());

        if (qualifiedTeachers.isEmpty()) {
            System.out.println("No qualified teacher found for subject: " + subject.getSubjectCode());
            return null;
        }

        // Find suitable rooms
        List<String> requiredEquipments = getRequiredEquipments(subject.getRoomType());
        List<Room> suitableRooms = rooms.stream()
                .filter(r -> r.meetsRequirements(subject.getRoomType(), 30, requiredEquipments))
                .collect(Collectors.toList());

        if (suitableRooms.isEmpty()) {
            System.out.println("No suitable room found for subject: " + subject.getSubjectCode());
            return null;
        }

        // Try to find a slot
        for (TimeSlot slot : availableSlots) {
            for (Teacher teacher : qualifiedTeachers) {
                // Check teacher availability
                if (!teacher.isAvailable(slot.getDay(), slot.getTimeRange())) {
                    continue;
                }

                // Check teacher workload
                if (!teacher.canTakeMoreHours()) {
                    continue;
                }

                for (Room room : suitableRooms) {
                    // Check room availability
                    if (!room.isSlotAvailable(slot)) {
                        continue;
                    }

                    // Check if slot is already taken for this section
                    if (isSectionBusy(section, slot)) {
                        continue;
                    }

                    // Create the timetable entry
                    return new TimetableEntry(subject, teacher, room, slot, section, 30);
                }
            }
        }

        return null;
    }

    private List<TimeSlot> generateTimeSlots() {
        List<TimeSlot> slots = new ArrayList<>();
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        // FIX: Added leading zeros to hours (09:00 instead of 9:00)
        String[] timeRanges = {"09:00-10:00", "10:00-11:00", "11:00-12:00",
                "12:00-13:00", "14:00-15:00", "15:00-16:00"};

        for (String day : days) {
            for (String timeRange : timeRanges) {
                String[] times = timeRange.split("-");
                slots.add(new TimeSlot(day, times[0], times[1]));
            }
        }

        return slots;
    }

    private List<String> getRequiredEquipments(String roomType) {
        List<String> equipments = new ArrayList<>();
        if ("LAB".equals(roomType)) {
            equipments.add("COMPUTERS");
        }
        return equipments;
    }

    private boolean isSectionBusy(String section, TimeSlot slot) {
        return generatedTimetable.stream()
                .anyMatch(e -> e.getStudentSection().equals(section) &&
                        e.getTimeSlot().equals(slot));
    }

    private void markSlotAsBooked(TimeSlot slot, Room room, Teacher teacher, String section) {
        room.bookSlot(slot);
        // Teacher availability will be handled separately
    }

    private void updateStudentTimetables() {
        List<Student> allStudents = database.getAllStudents();

        for (Student student : allStudents) {
            List<TimetableEntry> studentSchedule = generatedTimetable.stream()
                    .filter(e -> e.getStudentSection().equals(student.getSection()))
                    .filter(e -> student.getEnrolledSubjects().contains(e.getSubject().getSubjectCode()))
                    .collect(Collectors.toList());

            student.setPersonalTimetable(studentSchedule);
            database.updateStudent(student);
        }
    }

    public void publishTimetable() {
        database.saveTimetable(generatedTimetable);

        // Send notifications to all users
        NotificationService notificationService = new NotificationService(database);
        notificationService.notifyAllUsers("Timetable Published",
                "The new timetable has been published. Please check your schedule.",
                "SCHEDULE_CHANGE");

        System.out.println("Timetable published successfully!");
    }

    public List<TimetableEntry> getGeneratedTimetable() {
        return generatedTimetable;
    }
}