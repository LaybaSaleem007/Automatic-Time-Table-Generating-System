package Backend.logic.Model;

import java.util.Objects;

public class Subject {
    private String subjectCode;
    private String subjectName;
    private int credits;
    private int hoursPerWeek;
    private String department;
    private int semester;
    private String preferredTeacherId;
    private String roomType; // "LAB", "LECTURE_HALL", "SEMINAR_ROOM"

    public Subject(String subjectCode, String subjectName, int credits,
                   int hoursPerWeek, String department, int semester) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.credits = credits;
        this.hoursPerWeek = hoursPerWeek;
        this.department = department;
        this.semester = semester;
        this.roomType = "LECTURE_HALL";
    }

    // Getters and Setters
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public int getHoursPerWeek() { return hoursPerWeek; }
    public void setHoursPerWeek(int hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public String getPreferredTeacherId() { return preferredTeacherId; }
    public void setPreferredTeacherId(String preferredTeacherId) {
        this.preferredTeacherId = preferredTeacherId;
    }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    @Override
    public String toString() {
        return String.format("%s - %s (%d hrs/week)", subjectCode, subjectName, hoursPerWeek);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return Objects.equals(subjectCode, subject.subjectCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectCode);
    }
}