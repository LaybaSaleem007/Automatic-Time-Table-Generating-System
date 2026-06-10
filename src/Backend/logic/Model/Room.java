package Backend.logic.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Room {
    private String roomId;
    private String roomNumber;
    private String building;
    private int capacity;
    private String roomType; // "LAB", "LECTURE_HALL", "SEMINAR_ROOM"
    private List<String> equipments; // Projector, Computers, Smart Board, etc.
    private boolean isAvailable;
    private List<TimeSlot> bookedSlots;

    public Room(String roomNumber, String building, int capacity, String roomType) {
        this.roomId = roomNumber + "_" + building;
        this.roomNumber = roomNumber;
        this.building = building;
        this.capacity = capacity;
        this.roomType = roomType;
        this.equipments = new ArrayList<>();
        this.isAvailable = true;
        this.bookedSlots = new ArrayList<>();
    }

    // Getters and Setters
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public List<String> getEquipments() { return equipments; }
    public void setEquipments(List<String> equipments) { this.equipments = equipments; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public List<TimeSlot> getBookedSlots() { return bookedSlots; }

    public void addEquipment(String equipment) {
        this.equipments.add(equipment);
    }

    public boolean hasEquipment(String equipment) {
        return equipments.contains(equipment);
    }

    public boolean isSlotAvailable(TimeSlot timeSlot) {
        return !bookedSlots.contains(timeSlot);
    }

    public void bookSlot(TimeSlot timeSlot) {
        if (isSlotAvailable(timeSlot)) {
            bookedSlots.add(timeSlot);
        }
    }

    public void freeSlot(TimeSlot timeSlot) {
        bookedSlots.remove(timeSlot);
    }

    public boolean meetsRequirements(String requiredRoomType, int requiredCapacity, List<String> requiredEquipments) {
        if (!this.roomType.equals(requiredRoomType) && !requiredRoomType.equals("ANY")) {
            return false;
        }
        if (this.capacity < requiredCapacity) {
            return false;
        }
        for (String equipment : requiredEquipments) {
            if (!hasEquipment(equipment)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("Room %s (%s, Cap: %d, Type: %s)",
                roomNumber, building, capacity, roomType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(roomId, room.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }
}