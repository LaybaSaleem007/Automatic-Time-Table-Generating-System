package Model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TimeSlot {
    private String day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String slotId;

    // Formatter that handles times without leading zeros (e.g., "9:00" or "09:00")
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");

    public TimeSlot(String day, String startTime, String endTime) {
        this.day = day.toUpperCase();
        // FIX: Use the formatter to parse times
        this.startTime = LocalTime.parse(startTime, TIME_FORMATTER);
        this.endTime = LocalTime.parse(endTime, TIME_FORMATTER);
        this.slotId = generateSlotId();
    }

    public TimeSlot(String day, LocalTime startTime, LocalTime endTime) {
        this.day = day.toUpperCase();
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotId = generateSlotId();
    }

    private String generateSlotId() {
        return String.format("%s_%s_%s", day, startTime.toString(), endTime.toString());
    }

    // Getters
    public String getDay() { return day; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getSlotId() { return slotId; }

    public String getTimeRange() {
        return String.format("%s - %s", startTime.toString(), endTime.toString());
    }

    public boolean overlapsWith(TimeSlot other) {
        return this.day.equals(other.day) &&
                !(this.endTime.isBefore(other.startTime) ||
                        this.startTime.isAfter(other.endTime));
    }

    @Override
    public String toString() {
        return String.format("%s %s-%s", day, startTime, endTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return Objects.equals(slotId, timeSlot.slotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotId);
    }
}