package logic;

import Model.*;
import java.util.*;
import java.util.stream.Collectors;

public class RoomAllocator {

    public Room allocateRoom(Subject subject, TimeSlot timeSlot, List<Room> availableRooms,
                             List<TimetableEntry> existingSchedule) {

        List<String> requiredEquipments = getRequiredEquipments(subject.getRoomType());

        List<Room> suitableRooms = availableRooms.stream()
                .filter(room -> room.meetsRequirements(subject.getRoomType(), 30, requiredEquipments))
                .filter(room -> room.isSlotAvailable(timeSlot))
                .filter(room -> !isRoomBooked(room, timeSlot, existingSchedule))
                .sorted(Comparator.comparingInt(Room::getCapacity))
                .collect(Collectors.toList());

        return suitableRooms.isEmpty() ? null : suitableRooms.get(0);
    }

    public Room allocateRoomWithPreferences(String preferredRoomType, int requiredCapacity, List<String> requiredEquipments, TimeSlot timeSlot, List<Room> availableRooms, List<TimetableEntry> existingSchedule)
    {

        return availableRooms.stream()
                .filter(room -> room.meetsRequirements(preferredRoomType, requiredCapacity, requiredEquipments))
                .filter(room -> room.isSlotAvailable(timeSlot))
                .filter(room -> !isRoomBooked(room, timeSlot, existingSchedule))
                .findFirst()
                .orElse(null);
    }

    private boolean isRoomBooked(Room room, TimeSlot timeSlot, List<TimetableEntry> schedule) {
        return schedule.stream().anyMatch(entry ->
                entry.getRoom().equals(room) && entry.getTimeSlot().overlapsWith(timeSlot)
        );
    }

    private List<String> getRequiredEquipments(String roomType) {
        List<String> equipments = new ArrayList<>();
        switch (roomType) {
            case "LAB":
                equipments.add("COMPUTERS");
                equipments.add("PROJECTOR");
                break;
            case "LECTURE_HALL":
                equipments.add("PROJECTOR");
                break;
            case "SEMINAR_ROOM":
                equipments.add("SMART_BOARD");
                break;
        }
        return equipments;
    }

    public Map<Room, Integer> getRoomUtilization(List<TimetableEntry> timetable, List<Room> allRooms) {
        Map<Room, Integer> utilization = new HashMap<>();

        for (Room room : allRooms) {
            long hoursBooked = timetable.stream()
                    .filter(entry -> entry.getRoom().equals(room))
                    .count();
            utilization.put(room, (int) hoursBooked);
        }

        return utilization;
    }

    public List<Room> suggestAlternativeRooms(Room preferredRoom, TimeSlot timeSlot,
                                              List<Room> allRooms, List<TimetableEntry> schedule) {
        return allRooms.stream()
                .filter(room -> room.getRoomType().equals(preferredRoom.getRoomType()))
                .filter(room -> room.getCapacity() >= preferredRoom.getCapacity() * 0.8)
                .filter(room -> !room.equals(preferredRoom))
                .filter(room -> room.isSlotAvailable(timeSlot))
                .filter(room -> !isRoomBooked(room, timeSlot, schedule))
                .collect(Collectors.toList());
    }
}