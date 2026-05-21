package Model;

import java.util.ArrayList;
import java.util.List;

public class Admin extends User {
    private List<String> managedDepartments;
    private String adminLevel;

    public Admin(String name, String email, String password) {
        super(name, email, password, "ADMIN");
        this.managedDepartments = new ArrayList<>();
        this.adminLevel = "FULL";
    }

    public Admin(String name, String email, String password, String adminLevel) {
        super(name, email, password, "ADMIN");
        this.managedDepartments = new ArrayList<>();
        this.adminLevel = adminLevel;
    }

    public List<String> getManagedDepartments() { return managedDepartments; }
    public void setManagedDepartments(List<String> managedDepartments) {
        this.managedDepartments = managedDepartments;
    }

    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }

    public void addManagedDepartment(String department) {
        this.managedDepartments.add(department);
    }

    @Override
    public void displayDashboard() {
        System.out.println("\n=== ADMIN DASHBOARD ===");
        System.out.println("Welcome Admin: " + getName());
        System.out.println("Admin Level: " + adminLevel);
        System.out.println("Managed Departments: " + managedDepartments);
        System.out.println("1. Manage Subjects\n2. Manage Rooms\n3. Manage Teachers\n" + "4. Generate Timetable\n5. View All Timetables\n6. Approve Changes\n" + "7. Publish Timetable\n8. View Complaints\n9. Logout");
    }
}