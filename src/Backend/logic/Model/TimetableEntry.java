package Backend.logic.Model;

import java.util.Objects;

public class TimetableEntry {
    private String entryId;
    private Subject subject;
    private Teacher teacher;
    private Room room;
    private TimeSlot timeSlot;
    private String studentSection;
    private int studentCount;

    public TimetableEntry(Subject subject, Teacher teacher, Room room,
                          TimeSlot timeSlot, String studentSection, int studentCount) {
        this.entryId = generateEntryId();
        this.subject = subject;
        this.teacher = teacher;
        this.room = room;
        this.timeSlot = timeSlot;
        this.studentSection = studentSection;
        this.studentCount = studentCount;
    }

    private String generateEntryId() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    // Getters and Setters
    public String getEntryId() { return entryId; }
    public Subject getSubject() { return subject; }
    public Teacher getTeacher() { return teacher; }
    public Room getRoom() { return room; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public String getStudentSection() { return studentSection; }
    public int getStudentCount() { return studentCount; }

    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
    public void setRoom(Room room) { this.room = room; }
    public void setTimeSlot(TimeSlot timeSlot) { this.timeSlot = timeSlot; }

    public boolean hasConflict(TimetableEntry other) {
        // Check if same teacher is double-booked
        if (this.teacher.equals(other.teacher) &&
                this.timeSlot.overlapsWith(other.timeSlot)) {
            return true;
        }

        // Check if same room is double-booked
        if (this.room.equals(other.room) &&
                this.timeSlot.overlapsWith(other.timeSlot)) {
            return true;
        }

        // Check if same student section is double-booked
        if (this.studentSection.equals(other.studentSection) &&
                this.timeSlot.overlapsWith(other.timeSlot)) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Teacher: %s | Room: %s | %s | Section: %s",
                timeSlot, subject.getSubjectName(), teacher.getName(),
                room.getRoomNumber(), subject.getSubjectCode(), studentSection);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimetableEntry that = (TimetableEntry) o;
        return Objects.equals(entryId, that.entryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryId);
    }
}