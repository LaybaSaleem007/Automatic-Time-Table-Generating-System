import Model.*;
import TimeTable.TimetableDatabase;
import logic.SchedulingEngine;
import logic.NotificationService;
import logic.Complaint;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static TimetableDatabase database;
    private static SchedulingEngine scheduler;
    private static User currentUser;
    private static Scanner scanner;

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("  AUTOMATIC TIMETABLE GENERATING SYSTEM  ");
        System.out.println("=========================================");

        // Initialize system
        database = new TimetableDatabase();
        scheduler = new SchedulingEngine(database);
        scanner = new Scanner(System.in);

        // Main login loop
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showRoleMenu();
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n--- LOGIN ---");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("Choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice == 1) {
            login();
        } else if (choice == 2) {
            System.out.println("Goodbye!");
            System.exit(0);
        }
    }

    private static void login() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = database.getUserByEmail(email);
        if (user != null && user.login(email, password)) {
            currentUser = user;
            System.out.println("\n✅ Login successful! Welcome " + user.getName());
        } else {
            System.out.println("❌ Invalid credentials!");
        }
    }

    private static void showRoleMenu() {
        if (currentUser instanceof Admin) {
            showAdminMenu();
        } else if (currentUser instanceof Teacher) {
            showTeacherMenu();
        } else if (currentUser instanceof Student) {
            showStudentMenu();
        }
    }

    private static void showAdminMenu() {
        while (true) {
            currentUser.displayDashboard();
            System.out.print("\nChoice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    manageSubjects();
                    break;
                case 2:
                    manageRooms();
                    break;
                case 3:
                    manageTeachers();
                    break;
                case 4:
                    generateTimetable();
                    break;
                case 5:
                    viewAllTimetables();
                    break;
                case 6:
                    approveChanges();
                    break;
                case 7:
                    publishTimetable();
                    break;
                case 8:
                    viewComplaints();
                    break;
                case 9:
                    currentUser.logout();
                    currentUser = null;
                    System.out.println("Logged out successfully!");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void manageSubjects() {
        System.out.println("\n=== MANAGE SUBJECTS ===");
        System.out.println("1. Add Subject");
        System.out.println("2. View All Subjects");
        System.out.println("3. Delete Subject");
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.print("Subject Code: ");
            String code = scanner.nextLine();
            System.out.print("Subject Name: ");
            String name = scanner.nextLine();
            System.out.print("Credits: ");
            int credits = scanner.nextInt();
            System.out.print("Hours/Week: ");
            int hours = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Department: ");
            String dept = scanner.nextLine();
            System.out.print("Semester: ");
            int sem = scanner.nextInt();

            Subject subject = new Subject(code, name, credits, hours, dept, sem);
            database.addSubject(subject);
            System.out.println("✅ Subject added successfully!");
        } else if (choice == 2) {
            System.out.println("\n--- ALL SUBJECTS ---");
            for (Subject s : database.getAllSubjects()) {
                System.out.println(s);
            }
        } else if (choice == 3) {
            System.out.print("Subject Code to delete: ");
            String code = scanner.nextLine();
            database.deleteSubject(code);
            System.out.println("✅ Subject deleted!");
        }
    }

    private static void manageRooms() {
        System.out.println("\n=== MANAGE ROOMS ===");
        System.out.println("1. Add Room");
        System.out.println("2. View All Rooms");
        System.out.println("3. Delete Room");
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.print("Room Number: ");
            String number = scanner.nextLine();
            System.out.print("Building: ");
            String building = scanner.nextLine();
            System.out.print("Capacity: ");
            int capacity = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Room Type (LAB/LECTURE_HALL/SEMINAR_ROOM): ");
            String type = scanner.nextLine();

            Room room = new Room(number, building, capacity, type);
            database.addRoom(room);
            System.out.println("✅ Room added successfully!");
        } else if (choice == 2) {
            System.out.println("\n--- ALL ROOMS ---");
            for (Room r : database.getAllRooms()) {
                System.out.println(r);
            }
        } else if (choice == 3) {
            System.out.print("Room ID to delete: ");
            String id = scanner.nextLine();
            database.deleteRoom(id);
            System.out.println(" Room deleted!");
        }
    }

    private static void manageTeachers() {
        System.out.println("\n=== MANAGE TEACHERS ===");
        System.out.println("1. View All Teachers");
        System.out.println("2. Add Qualified Subject to Teacher");
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.println("\n--- ALL TEACHERS ---");
            for (Teacher t : database.getAllTeachers()) {
                System.out.println(t.getName() + " (" + t.getUserId() + ")");
                System.out.println("  Qualified Subjects: " + t.getQualifiedSubjects());
            }
        } else if (choice == 2) {
            System.out.print("Teacher ID: ");
            String teacherId = scanner.nextLine();
            Teacher teacher = database.getTeacher(teacherId);
            if (teacher != null) {
                System.out.print("Subject Code to add: ");
                String subjectCode = scanner.nextLine();
                teacher.addQualifiedSubject(subjectCode);
                database.updateTeacher(teacher);
                System.out.println("Subject added to teacher!");
            }
        }
    }

    private static void generateTimetable() {
        System.out.println("\n⏳ Generating timetable...");
        List<TimetableEntry> timetable = scheduler.generateTimetable();

        if (timetable != null && !timetable.isEmpty()) {
            System.out.println("✅ Timetable generated successfully with " + timetable.size() + " entries!");
            displayTimetable(timetable);
        } else {
            System.out.println(" Failed to generate timetable!");
        }
    }

    private static void viewAllTimetables() {
        List<TimetableEntry> timetable = database.getCurrentTimetable();
        if (timetable.isEmpty()) {
            System.out.println("No timetable generated yet. Please generate first.");
        } else {
            displayTimetable(timetable);
        }
    }

    private static void displayTimetable(List<TimetableEntry> timetable) {
        System.out.println("\n=== CURRENT TIMETABLE ===");
        System.out.printf("%-12s %-8s %-20s %-15s %-10s %-10s\n",
                "Day", "Time", "Subject", "Teacher", "Room", "Section");
        System.out.println("----------------------------------------------------------------");

        for (TimetableEntry entry : timetable) {
            System.out.printf("%-12s %-8s %-20s %-15s %-10s %-10s\n",
                    entry.getTimeSlot().getDay(),
                    entry.getTimeSlot().getTimeRange(),
                    truncate(entry.getSubject().getSubjectName(), 20),
                    truncate(entry.getTeacher().getName(), 15),
                    entry.getRoom().getRoomNumber(),
                    entry.getStudentSection());
        }
    }

    private static void approveChanges() {
        System.out.println("All pending changes approved!");
        NotificationService ns = new NotificationService(database);
        ns.notifyAllUsers("Changes Approved", "Schedule changes have been approved", "SUCCESS");
    }

    private static void publishTimetable() {
        scheduler.publishTimetable();
        System.out.println("✅ Timetable published to all users!");
    }

    private static void viewComplaints() {
        System.out.println("\n=== ALL COMPLAINTS ===");
        for (Complaint c : database.getAllComplaints()) {
            System.out.println(c);
            System.out.println("  Student: " + c.getStudentName());
            System.out.println("  Status: " + c.getStatus());
        }
    }

    private static void showTeacherMenu() {
        while (true) {
            currentUser.displayDashboard();
            System.out.print("\nChoice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    setTeacherAvailability();
                    break;
                case 2:
                    viewTeacherSchedule();
                    break;
                case 3:
                    requestScheduleChange();
                    break;
                case 4:
                    viewNotifications();
                    break;
                case 5:
                    currentUser.logout();
                    currentUser = null;
                    System.out.println("Logged out successfully!");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void setTeacherAvailability() {
        Teacher teacher = (Teacher) currentUser;
        System.out.println("\n=== SET AVAILABILITY ===");
        System.out.println("Current availability is set by default (Mon-Fri, 9 AM - 5 PM)");
        System.out.println("To customize, please contact admin.");
        System.out.println("✅ Your current availability: " + teacher.getAvailability().size() + " days available");
    }

    private static void viewTeacherSchedule() {
        Teacher teacher = (Teacher) currentUser;
        List<TimetableEntry> schedule = database.getTimetableForTeacher(teacher.getUserId());

        if (schedule.isEmpty()) {
            System.out.println("No classes assigned yet.");
        } else {
            System.out.println("\n=== YOUR SCHEDULE ===");
            for (TimetableEntry entry : schedule) {
                System.out.println(entry);
            }
        }
    }

    private static void requestScheduleChange() {
        System.out.print("Enter class details to request change: ");
        String details = scanner.nextLine();
        System.out.println("✅ Change request submitted to admin!");

        NotificationService ns = new NotificationService(database);
        ns.notifyAllUsers("Schedule Change Request", details, "WARNING");
    }

    private static void showStudentMenu() {
        while (true) {
            currentUser.displayDashboard();
            System.out.print("\nChoice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewStudentTimetable();
                    break;
                case 2:
                    checkRoomAndTime();
                    break;
                case 3:
                    submitComplaint();
                    break;
                case 4:
                    viewNotifications();
                    break;
                case 5:
                    currentUser.logout();
                    currentUser = null;
                    System.out.println("Logged out successfully!");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void viewStudentTimetable() {
        Student student = (Student) currentUser;
        List<TimetableEntry> timetable = database.getTimetableForStudent(student.getUserId());

        if (timetable.isEmpty()) {
            System.out.println("No timetable available yet. Please wait for generation.");
        } else {
            System.out.println("\n=== YOUR TIMETABLE ===");
            System.out.printf("%-12s %-8s %-20s %-15s %-10s\n",
                    "Day", "Time", "Subject", "Teacher", "Room");
            System.out.println("--------------------------------------------------------");
            for (TimetableEntry entry : timetable) {
                System.out.printf("%-12s %-8s %-20s %-15s %-10s\n",
                        entry.getTimeSlot().getDay(),
                        entry.getTimeSlot().getTimeRange(),
                        truncate(entry.getSubject().getSubjectName(), 20),
                        truncate(entry.getTeacher().getName(), 15),
                        entry.getRoom().getRoomNumber());
            }
        }
    }

    private static void checkRoomAndTime() {
        System.out.print("Enter day (e.g., MONDAY): ");
        String day = scanner.nextLine();
        System.out.print("Enter time (e.g., 10:00-11:00): ");
        String time = scanner.nextLine();

        List<TimetableEntry> timetable = database.getCurrentTimetable();
        TimetableEntry found = timetable.stream()
                .filter(e -> e.getTimeSlot().getDay().equalsIgnoreCase(day) &&
                        e.getTimeSlot().getTimeRange().equals(time))
                .findFirst()
                .orElse(null);

        if (found != null) {
            System.out.println("📚 Class: " + found.getSubject().getSubjectName());
            System.out.println("👨‍🏫 Teacher: " + found.getTeacher().getName());
            System.out.println("🏠 Room: " + found.getRoom().getRoomNumber() +
                    " (" + found.getRoom().getBuilding() + ")");
        } else {
            System.out.println("No class scheduled at that time.");
        }
    }

    private static void submitComplaint() {
        Student student = (Student) currentUser;
        System.out.print("Subject: ");
        String subject = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();

        Complaint complaint = new Complaint(student.getUserId(), student.getName(), subject, description);
        database.addComplaint(complaint);
        student.submitComplaint(complaint);

        System.out.println("✅ Complaint submitted! ID: " + complaint.getComplaintId());
    }

    private static void viewNotifications() {
        List<Notification> notifications = database.getNotificationsForUser(currentUser.getUserId());

        if (notifications.isEmpty()) {
            System.out.println("No notifications.");
        } else {
            System.out.println("\n=== YOUR NOTIFICATIONS ===");
            for (Notification n : notifications) {
                System.out.println("[" + n.getTimestamp().toLocalTime() + "] " +
                        n.getTitle() + ": " + n.getMessage());
            }
        }
    }

    private static String truncate(String str, int length) {
        if (str == null) return "";
        if (str.length() <= length) return str;
        return str.substring(0, length - 3) + "...";
    }
}