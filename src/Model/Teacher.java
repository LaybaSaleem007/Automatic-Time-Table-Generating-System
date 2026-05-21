package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Teacher extends User {
    private String department;
    private String designation;
    private List<String> qualifiedSubjects;
    private Map<String, List<String>> availability; // Day -> List of available time slots
    private int maxHoursPerWeek;
    private int assignedHours;
    private List<TimetableEntry> assignedSchedule;

    public Teacher(String name, String email, String password, String department, String designation) {
        super(name, email, password, "TEACHER");
        this.department = department;
        this.designation = designation;
        this.qualifiedSubjects = new ArrayList<>();
        this.availability = new HashMap<>();
        this.maxHoursPerWeek = 20;
        this.assignedHours = 0;
        this.assignedSchedule = new ArrayList<>();

        // Initialize default availability (all days, 9 AM to 5 PM)
        initializeDefaultAvailability();
    }

    private void initializeDefaultAvailability() {
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        String[] slots = {"9:00-10:00", "10:00-11:00", "11:00-12:00", "12:00-13:00",
                "14:00-15:00", "15:00-16:00", "16:00-17:00"};

        for (String day : days) {
            List<String> timeSlots = new ArrayList<>();
            for (String slot : slots) {
                timeSlots.add(slot);
            }
            availability.put(day, timeSlots);
        }
    }

    // Getters and Setters
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public List<String> getQualifiedSubjects() { return qualifiedSubjects; }
    public void setQualifiedSubjects(List<String> qualifiedSubjects) {
        this.qualifiedSubjects = qualifiedSubjects;
    }

    public Map<String, List<String>> getAvailability() { return availability; }
    public void setAvailability(Map<String, List<String>> availability) {
        this.availability = availability;
    }

    public int getMaxHoursPerWeek() { return maxHoursPerWeek; }
    public void setMaxHoursPerWeek(int maxHoursPerWeek) { this.maxHoursPerWeek = maxHoursPerWeek; }

    public int getAssignedHours() { return assignedHours; }
    public void setAssignedHours(int assignedHours) { this.assignedHours = assignedHours; }

    public List<TimetableEntry> getAssignedSchedule() { return assignedSchedule; }
    public void setAssignedSchedule(List<TimetableEntry> assignedSchedule) {
        this.assignedSchedule = assignedSchedule;
    }

    public void addQualifiedSubject(String subject) {
        this.qualifiedSubjects.add(subject);
    }

    public void setAvailabilityPreference(String day, List<String> timeSlots) {
        this.availability.put(day, timeSlots);
    }

    public boolean isAvailable(String day, String timeSlot) {
        return availability.containsKey(day) && availability.get(day).contains(timeSlot);
    }

    public boolean canTakeMoreHours() {
        return assignedHours < maxHoursPerWeek;
    }

    public void addAssignedHour() {
        this.assignedHours++;
    }

    public void removeAssignedHour() {
        if (this.assignedHours > 0) {
            this.assignedHours--;
        }
    }

    public void addToSchedule(TimetableEntry entry) {
        this.assignedSchedule.add(entry);
        addAssignedHour();
    }
 public void removeFromSchedule(TimetableEntry entry) {
        this.assignedSchedule.remove(entry);
        removeAssignedHour();
    }
    @Override
    public void displayDashboard() {
        System.out.println("\n=== TEACHER DASHBOARD ===");
        System.out.println("Welcome Teacher: " + getName());
        System.out.println("Department: " + department);
        System.out.println("Designation: " + designation);
        System.out.println("Assigned Hours: " + assignedHours + "/" + maxHoursPerWeek);
        System.out.println("1. Set Availability\n2. View My Schedule\n3. Request Schedule Change\n" +
                "4. View Notifications\n5. Logout");
    }
}