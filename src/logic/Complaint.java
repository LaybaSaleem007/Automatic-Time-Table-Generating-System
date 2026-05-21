package logic;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Complaint {
    private String complaintId;
    private String studentId;
    private String studentName;
    private String subject;
    private String description;
    private String status; // PENDING, IN_PROGRESS, RESOLVED, REJECTED
    private LocalDateTime submittedAt;
    private String adminResponse;

    public Complaint(String studentId, String studentName, String subject, String description) {
        this.complaintId = UUID.randomUUID().toString().substring(0, 8);
        this.studentId = studentId;
        this.studentName = studentName;
        this.subject = subject;
        this.description = description;
        this.status = "PENDING";
        this.submittedAt = LocalDateTime.now();
        this.adminResponse = "";
    }

    // Getters and Setters
    public String getComplaintId() { return complaintId; }
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getSubject() { return subject; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public String getAdminResponse() { return adminResponse; }

    public void setStatus(String status) { this.status = status; }
    public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }

    public void resolve(String response) {
        this.status = "RESOLVED";
        this.adminResponse = response;
    }

    public void reject(String reason) {
        this.status = "REJECTED";
        this.adminResponse = reason;
    }

    @Override
    public String toString() {
        return String.format("Complaint #%s: %s - %s (%s)",
                complaintId, subject, description.substring(0, Math.min(50, description.length())), status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Complaint complaint = (Complaint) o;
        return Objects.equals(complaintId, complaint.complaintId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(complaintId);
    }
}