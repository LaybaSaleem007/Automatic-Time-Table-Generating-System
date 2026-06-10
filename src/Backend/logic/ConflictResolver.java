package Backend.logic;

import Backend.logic.Model.*;
import TimeTable.TimetableDatabase;

import java.util.*;
import java.util.stream.Collectors;

public class ConflictResolver {

    public List<TimetableEntry> resolveAllConflicts(List<TimetableEntry> timetable, TimetableDatabase database) {
        List<TimetableEntry> resolvedTimetable = new ArrayList<>(timetable);
        boolean conflictFound;
        int maxIterations = 100;
        int iteration = 0;

        do {
            conflictFound = false;
            List<Conflict> conflicts = findConflictsWithDetails(resolvedTimetable);

            for (Conflict conflict : conflicts) {
                System.out.println("Resolving conflict between: " + conflict.entry1 + " and " + conflict.entry2);
                TimetableEntry resolved = resolveConflict(conflict.entry1, resolvedTimetable, database);

                if (resolved != null && !resolved.equals(conflict.entry1)) {
                    resolvedTimetable.remove(conflict.entry1);
                    resolvedTimetable.add(resolved);
                    conflictFound = true;
                }
            }
            iteration++;
        } while (conflictFound && iteration < maxIterations);

        return resolvedTimetable;
    }

    private List<Conflict> findConflictsWithDetails(List<TimetableEntry> timetable) {
        List<Conflict> conflicts = new ArrayList<>();

        for (int i = 0; i < timetable.size(); i++) {
            for (int j = i + 1; j < timetable.size(); j++) {
                if (timetable.get(i).hasConflict(timetable.get(j))) {
                    conflicts.add(new Conflict(timetable.get(i), timetable.get(j)));
                }
            }
        }
        return conflicts;
    }

    private TimetableEntry resolveConflict(TimetableEntry conflictingEntry, List<TimetableEntry> existingTimetable, TimetableDatabase database) {
        List<TimeSlot> alternativeSlots = generateAlternativeSlots(conflictingEntry.getTimeSlot());

        List<Teacher> alternativeTeachers = database.getAllTeachers().stream()
                .filter(t -> t.getQualifiedSubjects().contains(conflictingEntry.getSubject().getSubjectCode()))
                .filter(t -> !t.equals(conflictingEntry.getTeacher()))
                .collect(Collectors.toList());

        List<Room> alternativeRooms = database.getAllRooms().stream()
                .filter(r -> r.getRoomType().equals(conflictingEntry.getRoom().getRoomType()))
                .filter(r -> !r.equals(conflictingEntry.getRoom()))
                .collect(Collectors.toList());

        for (TimeSlot slot : alternativeSlots) {
            if (isSlotAvailable(conflictingEntry.getTeacher(), slot, existingTimetable) &&
                    isRoomAvailable(conflictingEntry.getRoom(), slot, existingTimetable) &&
                    !isSectionBusy(conflictingEntry.getStudentSection(), slot, existingTimetable)) {

                return new TimetableEntry(
                        conflictingEntry.getSubject(),
                        conflictingEntry.getTeacher(),
                        conflictingEntry.getRoom(),
                        slot,
                        conflictingEntry.getStudentSection(),
                        conflictingEntry.getStudentCount()
                );
            }

            for (Teacher teacher : alternativeTeachers) {
                if (isSlotAvailable(teacher, slot, existingTimetable)) {
                    for (Room room : alternativeRooms) {
                        if (isRoomAvailable(room, slot, existingTimetable) &&
                                !isSectionBusy(conflictingEntry.getStudentSection(), slot, existingTimetable)) {

                            return new TimetableEntry(
                                    conflictingEntry.getSubject(),
                                    teacher,
                                    room,
                                    slot,
                                    conflictingEntry.getStudentSection(),
                                    conflictingEntry.getStudentCount()
                            );
                        }
                    }
                }
            }
        }
        return null;
    }

    private List<TimeSlot> generateAlternativeSlots(TimeSlot originalSlot) {
        List<TimeSlot> alternatives = new ArrayList<>();
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        String[] timeSlots = {"9:00-10:00", "10:00-11:00", "11:00-12:00",
                "12:00-13:00", "14:00-15:00", "15:00-16:00", "16:00-17:00"};

        for (String time : timeSlots) {
            if (!time.equals(originalSlot.getTimeRange())) {
                String[] times = time.split("-");
                TimeSlot slot = new TimeSlot(originalSlot.getDay(), times[0], times[1]);
                alternatives.add(slot);
            }
        }

        for (String day : days) {
            if (!day.equals(originalSlot.getDay())) {
                String[] times = originalSlot.getTimeRange().split("-");
                TimeSlot slot = new TimeSlot(day, times[0], times[1]);
                alternatives.add(slot);
            }
        }
        return alternatives;
    }

    private boolean isSlotAvailable(Teacher teacher, TimeSlot slot, List<TimetableEntry> timetable) {
        return teacher.isAvailable(slot.getDay(), slot.getTimeRange()) &&
                timetable.stream().noneMatch(e ->
                        e.getTeacher().equals(teacher) &&
                                e.getTimeSlot().overlapsWith(slot)
                );
    }

    private boolean isRoomAvailable(Room room, TimeSlot slot, List<TimetableEntry> timetable) {
        return room.isSlotAvailable(slot) &&
                timetable.stream().noneMatch(e ->
                        e.getRoom().equals(room) &&
                                e.getTimeSlot().overlapsWith(slot)
                );
    }

    private boolean isSectionBusy(String section, TimeSlot slot, List<TimetableEntry> timetable) {
        return timetable.stream().anyMatch(e ->
                e.getStudentSection().equals(section) &&
                        e.getTimeSlot().overlapsWith(slot)
        );
    }

    private static class Conflict {
        TimetableEntry entry1;
        TimetableEntry entry2;
        Conflict(TimetableEntry entry1, TimetableEntry entry2) {
            this.entry1 = entry1;
            this.entry2 = entry2;
        }
    }
}