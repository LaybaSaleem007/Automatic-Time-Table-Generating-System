package Backend.logic.Model;

import Backend.logic.Complaint;

import java.util.*;

public class Student extends User {
    private String rollNumber;
    private String department;
    private int semester;
    private String section;
    private List<String> enrolledSubjects;
    private List<TimetableEntry> personalTimetable;
    private List<Complaint> complaints;  // Add this field

    public Student(String name, String email, String password,
                   String rollNumber, String department, int semester, String section) {
        super(name, email, password, "STUDENT");
        this.rollNumber = rollNumber;
        this.department = department;
        this.semester = semester;
        this.section = section;
        this.enrolledSubjects = new ArrayList<>();
        this.personalTimetable = new ArrayList<>();
        this.complaints = new ArrayList<>();  // Initialize complaints list
    }

    // Getters
    public String getRollNumber() { return rollNumber; }
    public String getDepartment() { return department; }
    public int getSemester() { return semester; }
    public String getSection() { return section; }
    public List<String> getEnrolledSubjects() { return enrolledSubjects; }
    public List<TimetableEntry> getPersonalTimetable() { return personalTimetable; }
    public List<Complaint> getComplaints() { return complaints; }  // Add this getter

    // Setters
    public void setSection(String section) { this.section = section; }
    public void setSemester(int semester) { this.semester = semester; }

    public void setPersonalTimetable(List<TimetableEntry> timetable) {
        this.personalTimetable = new ArrayList<>(timetable);
    }

    public void enrollSubject(String subjectCode) {
        if (!enrolledSubjects.contains(subjectCode)) {
            enrolledSubjects.add(subjectCode);
        }
    }

    // Add this method to fix the error
    public void submitComplaint(Complaint complaint) {
        this.complaints.add(complaint);
        System.out.println("Complaint recorded for student: " + this.getName());
    }

    public void viewTimetable() {
        if (personalTimetable.isEmpty()) {
            System.out.println("No timetable available.");
        } else {
            System.out.println("\n=== Timetable for " + getName() + " ===");
            for (TimetableEntry entry : personalTimetable) {
                System.out.println(entry);
            }
        }
    }

    @Override
    public void displayDashboard() {
        System.out.println("\n=========================================");
        System.out.println("  STUDENT DASHBOARD - " + getName().toUpperCase());
        System.out.println("=========================================");
        System.out.println("Roll Number: " + rollNumber);
        System.out.println("Department: " + department);
        System.out.println("Semester: " + semester);
        System.out.println("Section: " + section);
        System.out.println("=========================================");
        System.out.println("1. View My Timetable");
        System.out.println("2. Check Room/Time");
        System.out.println("3. Submit Complaint");
        System.out.println("4. View Notifications");
        System.out.println("5. Logout");
        System.out.println("=========================================");
    }
}