package TimeTable;
import Model.*;
import logic.Complaint;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TimetableDatabase {
    // Using ConcurrentHashMap for thread-safe operations
    private Map<String, User> users;
    private Map<String, Teacher> teachers;
    private Map<String, Student> students;
    private Map<String, Admin> admins;
    private Map<String, Subject> subjects;
    private Map<String, Room> rooms;
    private List<TimetableEntry> currentTimetable;
    private Map<String, List<Notification>> userNotifications;
    private Map<String, Complaint> complaints;

    public TimetableDatabase() {
        this.users = new ConcurrentHashMap<>();
        this.teachers = new ConcurrentHashMap<>();
        this.students = new ConcurrentHashMap<>();
        this.admins = new ConcurrentHashMap<>();
        this.subjects = new ConcurrentHashMap<>();
        this.rooms = new ConcurrentHashMap<>();
        this.currentTimetable = new ArrayList<>();
        this.userNotifications = new ConcurrentHashMap<>();
        this.complaints = new ConcurrentHashMap<>();

        // Initialize with sample data
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Add sample rooms
        Room room101 = new Room("101", "Main Building", 40, "LECTURE_HALL");
        room101.addEquipment("PROJECTOR");
        room101.addEquipment("SMART_BOARD");
        rooms.put(room101.getRoomId(), room101);

        Room room102 = new Room("102", "Main Building", 35, "LECTURE_HALL");
        room102.addEquipment("PROJECTOR");
        rooms.put(room102.getRoomId(), room102);

        Room lab201 = new Room("201", "Science Block", 30, "LAB");
        lab201.addEquipment("COMPUTERS");
        lab201.addEquipment("PROJECTOR");
        rooms.put(lab201.getRoomId(), lab201);

        // Add sample subjects
        Subject oop = new Subject("CS301", "Object Oriented Programming", 3, 3, "CS", 5);
        subjects.put(oop.getSubjectCode(), oop);

        Subject dsa = new Subject("CS302", "Data Structures & Algorithms", 3, 3, "CS", 5);
        subjects.put(dsa.getSubjectCode(), dsa);

        Subject db = new Subject("CS303", "Database Systems", 3, 3, "CS", 5);
        subjects.put(db.getSubjectCode(), db);

        // Add sample teachers
        Teacher teacher1 = new Teacher("Prof. Ahmed", "ahmed@uet.edu", "pass123", "CS", "Professor");
        teacher1.addQualifiedSubject("CS301");
        teacher1.addQualifiedSubject("CS302");
        teachers.put(teacher1.getUserId(), teacher1);
        users.put(teacher1.getUserId(), teacher1);

        Teacher teacher2 = new Teacher("Dr. Sarah", "sarah@uet.edu", "pass123", "CS", "Associate Professor");
        teacher2.addQualifiedSubject("CS302");
        teacher2.addQualifiedSubject("CS303");
        teachers.put(teacher2.getUserId(), teacher2);
        users.put(teacher2.getUserId(), teacher2);

        Teacher teacher3 = new Teacher("Ms. Fatima", "fatima@uet.edu", "pass123", "CS", "Assistant Professor");
        teacher3.addQualifiedSubject("CS301");
        teacher3.addQualifiedSubject("CS303");
        teachers.put(teacher3.getUserId(), teacher3);
        users.put(teacher3.getUserId(), teacher3);

        // Add sample students
        Student student1 = new Student("Ali Raza", "ali@uet.edu", "pass123", "2025-CYS-73", "CS", 5, "A");
        student1.enrollSubject("CS301");
        student1.enrollSubject("CS302");
        student1.enrollSubject("CS303");
        students.put(student1.getUserId(), student1);
        users.put(student1.getUserId(), student1);

        Student student2 = new Student("Sana Khan", "sana@uet.edu", "pass123", "2025-CYS-74", "CS", 5, "A");
        student2.enrollSubject("CS301");
        student2.enrollSubject("CS302");
        students.put(student2.getUserId(), student2);
        users.put(student2.getUserId(), student2);

        // Add sample admin
        Admin admin = new Admin("Admin User", "admin@uet.edu", "admin123", "FULL");
        admins.put(admin.getUserId(), admin);
        users.put(admin.getUserId(), admin);
    }

    // User Management
    public void addUser(User user) {
        users.put(user.getUserId(), user);
        if (user instanceof Teacher) {
            teachers.put(user.getUserId(), (Teacher) user);
        } else if (user instanceof Student) {
            students.put(user.getUserId(), (Student) user);
        } else if (user instanceof Admin) {
            admins.put(user.getUserId(), (Admin) user);
        }
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public User getUserByEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    // Teacher Management
    public List<Teacher> getAllTeachers() {
        return new ArrayList<>(teachers.values());
    }

    public Teacher getTeacher(String teacherId) {
        return teachers.get(teacherId);
    }

    public void updateTeacher(Teacher teacher) {
        teachers.put(teacher.getUserId(), teacher);
        users.put(teacher.getUserId(), teacher);
    }

    public void addTeacher(Teacher teacher) {
        teachers.put(teacher.getUserId(), teacher);
        users.put(teacher.getUserId(), teacher);
    }

    // Student Management
    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    public Student getStudent(String studentId) {
        return students.get(studentId);
    }

    public void updateStudent(Student student) {
        students.put(student.getUserId(), student);
        users.put(student.getUserId(), student);
    }

    // Admin Management
    public List<Admin> getAllAdmins() {
        return new ArrayList<>(admins.values());
    }

    // Subject Management
    public void addSubject(Subject subject) {
        subjects.put(subject.getSubjectCode(), subject);
    }

    public Subject getSubject(String subjectCode) {
        return subjects.get(subjectCode);
    }

    public List<Subject> getAllSubjects() {
        return new ArrayList<>(subjects.values());
    }

    public void updateSubject(Subject subject) {
        subjects.put(subject.getSubjectCode(), subject);
    }

    public void deleteSubject(String subjectCode) {
        subjects.remove(subjectCode);
    }

    // Room Management
    public void addRoom(Room room) {
        rooms.put(room.getRoomId(), room);
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public void updateRoom(Room room) {
        rooms.put(room.getRoomId(), room);
    }

    public void deleteRoom(String roomId) {
        rooms.remove(roomId);
    }

    public List<Room> getAvailableRooms(TimeSlot timeSlot) {
        return rooms.values().stream()
                .filter(room -> room.isSlotAvailable(timeSlot))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    // Timetable Management
    public void saveTimetable(List<TimetableEntry> timetable) {
        this.currentTimetable = new ArrayList<>(timetable);
    }

    public List<TimetableEntry> getCurrentTimetable() {
        return new ArrayList<>(currentTimetable);
    }

    public List<TimetableEntry> getTimetableForTeacher(String teacherId) {
        return currentTimetable.stream()
                .filter(entry -> entry.getTeacher().getUserId().equals(teacherId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<TimetableEntry> getTimetableForStudent(String studentId) {
        Student student = getStudent(studentId);
        if (student != null) {
            return student.getPersonalTimetable();
        }
        return new ArrayList<>();
    }

    public void addTimetableEntry(TimetableEntry entry) {
        currentTimetable.add(entry);
    }

    public void removeTimetableEntry(TimetableEntry entry) {
        currentTimetable.remove(entry);
    }

    // Notification Management
    public void addNotification(Notification notification) {
        userNotifications.computeIfAbsent(notification.getUserId(), k -> new ArrayList<>())
                .add(notification);
    }

    public List<Notification> getNotificationsForUser(String userId) {
        return userNotifications.getOrDefault(userId, new ArrayList<>());
    }

    public void markNotificationRead(String notificationId) {
        for (List<Notification> notifications : userNotifications.values()) {
            for (Notification n : notifications) {
                if (n.getNotificationId().equals(notificationId)) {
                    n.markAsRead();
                    return;
                }
            }
        }
    }

    // Complaint Management
    public void addComplaint(Complaint complaint) {
        complaints.put(complaint.getComplaintId(), complaint);
    }

    public Complaint getComplaint(String complaintId) {
        return complaints.get(complaintId);
    }

    public List<Complaint> getAllComplaints() {
        return new ArrayList<>(complaints.values());
    }

    public List<Complaint> getComplaintsByStudent(String studentId) {
        return complaints.values().stream()
                .filter(c -> c.getStudentId().equals(studentId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Complaint> getPendingComplaints() {
        return complaints.values().stream()
                .filter(c -> c.getStatus().equals("PENDING"))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void resolveComplaint(String complaintId, String response) {
        Complaint complaint = complaints.get(complaintId);
        if (complaint != null) {
            complaint.resolve(response);
        }
    }
    public void clearAllData() {
        users.clear();
        teachers.clear();
        students.clear();
        admins.clear();
        subjects.clear();
        rooms.clear();
        currentTimetable.clear();
        userNotifications.clear();
        complaints.clear();
    }

    public void printDatabaseStatus() {
        System.out.println("\n=== DATABASE STATUS ===");
        System.out.println("Users: " + users.size());
        System.out.println("Teachers: " + teachers.size());
        System.out.println("Students: " + students.size());
        System.out.println("Admins: " + admins.size());
        System.out.println("Subjects: " + subjects.size());
        System.out.println("Rooms: " + rooms.size());
        System.out.println("Timetable Entries: " + currentTimetable.size());
        System.out.println("=======================\n");
    }
}