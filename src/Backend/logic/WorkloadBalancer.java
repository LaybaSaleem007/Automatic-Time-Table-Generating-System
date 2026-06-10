package Backend.logic;

import Backend.logic.Model.*;
import TimeTable.TimetableDatabase;

import java.util.*;
import java.util.stream.Collectors;

public class WorkloadBalancer {

    private static final int MAX_HOURS_PER_TEACHER = 20;
    private static final int MIN_HOURS_PER_TEACHER = 12;

    public void balanceWorkload(List<TimetableEntry> timetable, TimetableDatabase database) {
        Map<Teacher, Integer> teacherWorkload = calculateWorkload(timetable);
        List<Teacher> allTeachers = database.getAllTeachers();

        List<Teacher> overworked = new ArrayList<>();
        List<Teacher> underworked = new ArrayList<>();

        for (Map.Entry<Teacher, Integer> entry : teacherWorkload.entrySet()) {
            if (entry.getValue() > MAX_HOURS_PER_TEACHER) {
                overworked.add(entry.getKey());
            } else if (entry.getValue() < MIN_HOURS_PER_TEACHER) {
                underworked.add(entry.getKey());
            }
        }

        for (Teacher over : overworked) {
            List<TimetableEntry> overTeacherClasses = timetable.stream()
                    .filter(entry -> entry.getTeacher().equals(over))
                    .collect(Collectors.toList());

            for (TimetableEntry entry : overTeacherClasses) {
                if (teacherWorkload.get(over) <= MAX_HOURS_PER_TEACHER) {
                    break;
                }

                for (Teacher under : underworked) {
                    if (under.getQualifiedSubjects().contains(entry.getSubject().getSubjectCode())) {
                        entry.setTeacher(under);
                        teacherWorkload.put(over, teacherWorkload.get(over) - 1);
                        teacherWorkload.put(under, teacherWorkload.getOrDefault(under, 0) + 1);

                        Notification notif = new Notification(
                                under.getUserId(),
                                "New Class Assigned",
                                "You have been assigned " + entry.getSubject().getSubjectName(),
                                "SCHEDULE_CHANGE"
                        );
                        database.addNotification(notif);
                        break;
                    }
                }
            }
        }

        for (Teacher teacher : allTeachers) {
            teacher.setAssignedHours(teacherWorkload.getOrDefault(teacher, 0));
            database.updateTeacher(teacher);
        }
    }

    public Map<Teacher, Integer> calculateWorkload(List<TimetableEntry> timetable) {
        Map<Teacher, Integer> workload = new HashMap<>();
        for (TimetableEntry entry : timetable) {
            workload.put(entry.getTeacher(), workload.getOrDefault(entry.getTeacher(), 0) + 1);
        }
        return workload;
    }

    public boolean isWorkloadBalanced(List<TimetableEntry> timetable, List<Teacher> teachers) {
        Map<Teacher, Integer> workload = calculateWorkload(timetable);
        for (Teacher teacher : teachers) {
            int hours = workload.getOrDefault(teacher, 0);
            if (hours > MAX_HOURS_PER_TEACHER || hours < MIN_HOURS_PER_TEACHER) {
                return false;
            }
        }
        return true;
    }

    public Map<String, Double> getWorkloadStatistics(List<TimetableEntry> timetable, List<Teacher> teachers) {
        Map<Teacher, Integer> workload = calculateWorkload(timetable);
        double average = teachers.stream()
                .mapToInt(t -> workload.getOrDefault(t, 0))
                .average()
                .orElse(0);
        int max = teachers.stream()
                .mapToInt(t -> workload.getOrDefault(t, 0))
                .max()
                .orElse(0);
        int min = teachers.stream()
                .mapToInt(t -> workload.getOrDefault(t, 0))
                .min()
                .orElse(0);

        Map<String, Double> stats = new HashMap<>();
        stats.put("average", average);
        stats.put("max", (double) max);
        stats.put("min", (double) min);
        stats.put("standardDeviation", calculateStdDev(workload, teachers, average));
        return stats;
    }

    private double calculateStdDev(Map<Teacher, Integer> workload, List<Teacher> teachers, double mean) {
        double variance = teachers.stream()
                .mapToDouble(t -> Math.pow(workload.getOrDefault(t, 0) - mean, 2))
                .average()
                .orElse(0);
        return Math.sqrt(variance);
    }

    public void suggestWorkloadAdjustments(List<TimetableEntry> timetable, List<Teacher> teachers) {
        Map<Teacher, Integer> workload = calculateWorkload(timetable);
        System.out.println("\n=== Workload Analysis ===");
        for (Teacher teacher : teachers) {
            int hours = workload.getOrDefault(teacher, 0);
            String status;
            if (hours > MAX_HOURS_PER_TEACHER) {
                status = "OVERWORKED - Need to reduce by " + (hours - MAX_HOURS_PER_TEACHER) + " hours";
            } else if (hours < MIN_HOURS_PER_TEACHER) {
                status = "UNDERWORKED - Can take " + (MIN_HOURS_PER_TEACHER - hours) + " more hours";
            } else {
                status = "BALANCED";
            }
            System.out.println(teacher.getName() + ": " + hours + " hours - " + status);
        }
    }
}