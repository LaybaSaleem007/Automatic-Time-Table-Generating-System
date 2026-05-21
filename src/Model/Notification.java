package Model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Notification {
    private String notificationId;
    private String userId;
    private String title;
    private String message;
    private String type; // INFO, WARNING, SUCCESS, SCHEDULE_CHANGE
    private LocalDateTime timestamp;
    private boolean isRead;

    public Notification(String userId, String title, String message, String type) {
        this.notificationId = UUID.randomUUID().toString().substring(0, 8);
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    // Getters and Setters
    public String getNotificationId() { return notificationId; }
    public String getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }

    public void markAsRead() { this.isRead = true; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", timestamp.toLocalTime(), title, message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(notificationId, that.notificationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId);
    }
}