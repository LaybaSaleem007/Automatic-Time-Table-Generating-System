package logic;

import Model.*;
import TimeTable.TimetableDatabase;  // Add this import
import java.util.List;

public class NotificationService {

    private TimeTable.TimetableDatabase database;  // Change field type

    public NotificationService(TimeTable.TimetableDatabase database) {  // Change constructor parameter
        this.database = database;
    }

    public void notifyUser(String userId, String title, String message, String type) {
        Notification notification = new Notification(userId, title, message, type);
        database.addNotification(notification);
        System.out.println("Notification sent to user " + userId + ": " + title);
    }

    public void notifyAllUsers(String title, String message, String type) {
        List<User> allUsers = database.getAllUsers();

        for (User user : allUsers) {
            notifyUser(user.getUserId(), title, message, type);
        }

        System.out.println("Notification broadcast to " + allUsers.size() + " users");
    }

    public void notifyTeachers(String title, String message, String type) {
        List<Teacher> teachers = database.getAllTeachers();

        for (Teacher teacher : teachers) {
            notifyUser(teacher.getUserId(), title, message, type);
        }

        System.out.println("Notification sent to " + teachers.size() + " teachers");
    }

    public void notifyStudents(String title, String message, String type) {
        List<Student> students = database.getAllStudents();

        for (Student student : students) {
            notifyUser(student.getUserId(), title, message, type);
        }

        System.out.println("Notification sent to " + students.size() + " students");
    }

    public void notifySection(String section, String title, String message, String type) {
        List<Student> students = database.getAllStudents();

        for (Student student : students) {
            if (student.getSection().equals(section)) {
                notifyUser(student.getUserId(), title, message, type);
            }
        }
    }

    public void notifyOnScheduleChange(TimetableEntry oldEntry, TimetableEntry newEntry) {
        // Notify teacher
        notifyUser(oldEntry.getTeacher().getUserId(),
                "Schedule Change - " + oldEntry.getSubject().getSubjectName(),
                "Your class has been moved from " + oldEntry.getTimeSlot() + " to " + newEntry.getTimeSlot(),
                "SCHEDULE_CHANGE");

        // Notify students in that section
        notifySection(oldEntry.getStudentSection(),
                "Timetable Update",
                "Your " + oldEntry.getSubject().getSubjectName() + " class has been rescheduled",
                "SCHEDULE_CHANGE");
    }

    public List<Notification> getUserNotifications(String userId) {
        return database.getNotificationsForUser(userId);
    }

    public void markNotificationAsRead(String notificationId) {
        database.markNotificationRead(notificationId);
    }
}