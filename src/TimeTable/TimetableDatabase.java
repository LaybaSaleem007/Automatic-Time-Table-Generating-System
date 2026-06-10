package TimeTable;

import Backend.logic.Model.*;
import Backend.logic.Complaint;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class TimetableDatabase {
    private Connection conn;

    public TimetableDatabase() {
        this.conn = DBConnection.getConnection();
        if (this.conn == null) {
            System.out.println("❌ Failed to connect to database!");
        } else {
            System.out.println("✅ Database connected successfully!");
        }
    }

    // ========== USER MANAGEMENT ==========

    public void addUser(User user) {
        String query = "INSERT INTO users (user_id, name, email, password, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole());
            ps.executeUpdate();

            if (user instanceof Teacher) {
                addTeacherToDB((Teacher) user);
            } else if (user instanceof Student) {
                addStudentToDB((Student) user);
            } else if (user instanceof Admin) {
                addAdminToDB((Admin) user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addTeacherToDB(Teacher teacher) {
        String query = "INSERT INTO teachers (user_id, department, designation, max_hours_per_week, assigned_hours) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, teacher.getUserId());
            ps.setString(2, teacher.getDepartment());
            ps.setString(3, teacher.getDesignation());
            ps.setInt(4, teacher.getMaxHoursPerWeek());
            ps.setInt(5, teacher.getAssignedHours());
            ps.executeUpdate();

            for (String subject : teacher.getQualifiedSubjects()) {
                String qualQuery = "INSERT INTO teacher_qualifications (teacher_id, subject_code) VALUES (?, ?)";
                try (PreparedStatement ps2 = conn.prepareStatement(qualQuery)) {
                    ps2.setString(1, teacher.getUserId());
                    ps2.setString(2, subject);
                    ps2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addStudentToDB(Student student) {
        String query = "INSERT INTO students (user_id, roll_number, department, semester, section) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, student.getUserId());
            ps.setString(2, student.getRollNumber());
            ps.setString(3, student.getDepartment());
            ps.setInt(4, student.getSemester());
            ps.setString(5, student.getSection());
            ps.executeUpdate();

            for (String subject : student.getEnrolledSubjects()) {
                String subQuery = "INSERT INTO student_subjects (student_id, subject_code) VALUES (?, ?)";
                try (PreparedStatement ps2 = conn.prepareStatement(subQuery)) {
                    ps2.setString(1, student.getUserId());
                    ps2.setString(2, subject);
                    ps2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addAdminToDB(Admin admin) {
        String query = "INSERT INTO admins (user_id, admin_level) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, admin.getUserId());
            ps.setString(2, admin.getAdminLevel());
            ps.executeUpdate();

            for (String dept : admin.getManagedDepartments()) {
                String deptQuery = "INSERT INTO admin_departments (admin_id, department) VALUES (?, ?)";
                try (PreparedStatement ps2 = conn.prepareStatement(deptQuery)) {
                    ps2.setString(1, admin.getUserId());
                    ps2.setString(2, dept);
                    ps2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUserByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String userId = rs.getString("user_id");
                String role = rs.getString("role");

                if ("ADMIN".equals(role)) {
                    return getAdminById(userId);
                } else if ("TEACHER".equals(role)) {
                    return getTeacherById(userId);
                } else if ("STUDENT".equals(role)) {
                    return getStudentById(userId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Teacher getTeacherById(String teacherId) {
        String query = "SELECT * FROM teachers WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, teacherId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String userQuery = "SELECT * FROM users WHERE user_id = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(userQuery)) {
                    ps2.setString(1, teacherId);
                    ResultSet rs2 = ps2.executeQuery();
                    if (rs2.next()) {
                        Teacher teacher = new Teacher(
                                rs2.getString("name"),
                                rs2.getString("email"),
                                rs2.getString("password"),
                                rs.getString("department"),
                                rs.getString("designation")
                        );
                        teacher.setUserId(teacherId);
                        teacher.setAssignedHours(rs.getInt("assigned_hours"));

                        String qualQuery = "SELECT subject_code FROM teacher_qualifications WHERE teacher_id = ?";
                        try (PreparedStatement ps3 = conn.prepareStatement(qualQuery)) {
                            ps3.setString(1, teacherId);
                            ResultSet rs3 = ps3.executeQuery();
                            List<String> quals = new ArrayList<>();
                            while (rs3.next()) {
                                quals.add(rs3.getString("subject_code"));
                            }
                            teacher.setQualifiedSubjects(quals);
                        }

                        // Load teacher availability
                        String availQuery = "SELECT day, time_slot FROM teacher_availability WHERE teacher_id = ?";
                        try (PreparedStatement ps4 = conn.prepareStatement(availQuery)) {
                            ps4.setString(1, teacherId);
                            ResultSet rs4 = ps4.executeQuery();
                            Map<String, List<String>> availability = new HashMap<>();
                            while (rs4.next()) {
                                String day = rs4.getString("day");
                                String timeSlot = rs4.getString("time_slot");
                                availability.computeIfAbsent(day, k -> new ArrayList<>()).add(timeSlot);
                            }
                            if (!availability.isEmpty()) {
                                teacher.setAvailability(availability);
                            }
                        }

                        return teacher;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Student getStudentById(String studentId) {
        String query = "SELECT * FROM students WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String userQuery = "SELECT * FROM users WHERE user_id = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(userQuery)) {
                    ps2.setString(1, studentId);
                    ResultSet rs2 = ps2.executeQuery();
                    if (rs2.next()) {
                        Student student = new Student(
                                rs2.getString("name"),
                                rs2.getString("email"),
                                rs2.getString("password"),
                                rs.getString("roll_number"),
                                rs.getString("department"),
                                rs.getInt("semester"),
                                rs.getString("section")
                        );
                        student.setUserId(studentId);

                        String subQuery = "SELECT subject_code FROM student_subjects WHERE student_id = ?";
                        try (PreparedStatement ps3 = conn.prepareStatement(subQuery)) {
                            ps3.setString(1, studentId);
                            ResultSet rs3 = ps3.executeQuery();
                            while (rs3.next()) {
                                student.enrollSubject(rs3.getString("subject_code"));
                            }
                        }
                        return student;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Admin getAdminById(String adminId) {
        String query = "SELECT * FROM admins WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, adminId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String userQuery = "SELECT * FROM users WHERE user_id = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(userQuery)) {
                    ps2.setString(1, adminId);
                    ResultSet rs2 = ps2.executeQuery();
                    if (rs2.next()) {
                        Admin admin = new Admin(
                                rs2.getString("name"),
                                rs2.getString("email"),
                                rs2.getString("password"),
                                rs.getString("admin_level")
                        );
                        admin.setUserId(adminId);
                        return admin;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                User user = getUserByEmail(rs.getString("email"));
                if (user != null) users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // ========== TEACHER MANAGEMENT ==========

    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        if (conn == null) return teachers;

        String query = "SELECT user_id FROM teachers";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Teacher teacher = getTeacherById(rs.getString("user_id"));
                if (teacher != null) teachers.add(teacher);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teachers;
    }

    public Teacher getTeacher(String teacherId) {
        return getTeacherById(teacherId);
    }

    public void updateTeacher(Teacher teacher) {
        String query = "UPDATE teachers SET department = ?, designation = ?, assigned_hours = ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, teacher.getDepartment());
            ps.setString(2, teacher.getDesignation());
            ps.setInt(3, teacher.getAssignedHours());
            ps.setString(4, teacher.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ========== STUDENT MANAGEMENT ==========

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        if (conn == null) return students;

        String query = "SELECT user_id FROM students";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Student student = getStudentById(rs.getString("user_id"));
                if (student != null) students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public Student getStudent(String studentId) {
        return getStudentById(studentId);
    }

    public void updateStudent(Student student) {
        String query = "UPDATE students SET semester = ?, section = ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, student.getSemester());
            ps.setString(2, student.getSection());
            ps.setString(3, student.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ========== SUBJECT MANAGEMENT ==========

    public void addSubject(Subject subject) {
        String query = "INSERT INTO subjects (subject_code, subject_name, credits, hours_per_week, department, semester, room_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, subject.getSubjectCode());
            ps.setString(2, subject.getSubjectName());
            ps.setInt(3, subject.getCredits());
            ps.setInt(4, subject.getHoursPerWeek());
            ps.setString(5, subject.getDepartment());
            ps.setInt(6, subject.getSemester());
            ps.setString(7, subject.getRoomType());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Subject getSubject(String subjectCode) {
        String query = "SELECT * FROM subjects WHERE subject_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, subjectCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Subject subject = new Subject(
                        rs.getString("subject_code"),
                        rs.getString("subject_name"),
                        rs.getInt("credits"),
                        rs.getInt("hours_per_week"),
                        rs.getString("department"),
                        rs.getInt("semester")
                );
                subject.setRoomType(rs.getString("room_type"));
                return subject;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        if (conn == null) return subjects;

        String query = "SELECT * FROM subjects";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Subject subject = new Subject(
                        rs.getString("subject_code"),
                        rs.getString("subject_name"),
                        rs.getInt("credits"),
                        rs.getInt("hours_per_week"),
                        rs.getString("department"),
                        rs.getInt("semester")
                );
                subject.setRoomType(rs.getString("room_type"));
                subjects.add(subject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    public void updateSubject(Subject subject) {
        String query = "UPDATE subjects SET subject_name = ?, credits = ?, hours_per_week = ?, department = ?, semester = ?, room_type = ? WHERE subject_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, subject.getSubjectName());
            ps.setInt(2, subject.getCredits());
            ps.setInt(3, subject.getHoursPerWeek());
            ps.setString(4, subject.getDepartment());
            ps.setInt(5, subject.getSemester());
            ps.setString(6, subject.getRoomType());
            ps.setString(7, subject.getSubjectCode());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteSubject(String subjectCode) {
        String query = "DELETE FROM subjects WHERE subject_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, subjectCode);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ========== ROOM MANAGEMENT ==========

    public void addRoom(Room room) {
        String query = "INSERT INTO rooms (room_id, room_number, building, capacity, room_type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, room.getRoomId());
            ps.setString(2, room.getRoomNumber());
            ps.setString(3, room.getBuilding());
            ps.setInt(4, room.getCapacity());
            ps.setString(5, room.getRoomType());
            ps.executeUpdate();

            for (String equipment : room.getEquipments()) {
                String eqQuery = "INSERT INTO room_equipments (room_id, equipment) VALUES (?, ?)";
                try (PreparedStatement ps2 = conn.prepareStatement(eqQuery)) {
                    ps2.setString(1, room.getRoomId());
                    ps2.setString(2, equipment);
                    ps2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Room getRoom(String roomId) {
        String query = "SELECT * FROM rooms WHERE room_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Room room = new Room(
                        rs.getString("room_number"),
                        rs.getString("building"),
                        rs.getInt("capacity"),
                        rs.getString("room_type")
                );
                room.setRoomId(roomId);

                String eqQuery = "SELECT equipment FROM room_equipments WHERE room_id = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(eqQuery)) {
                    ps2.setString(1, roomId);
                    ResultSet rs2 = ps2.executeQuery();
                    while (rs2.next()) {
                        room.addEquipment(rs2.getString("equipment"));
                    }
                }
                return room;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        if (conn == null) return rooms;

        String query = "SELECT room_id FROM rooms";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Room room = getRoom(rs.getString("room_id"));
                if (room != null) rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public void updateRoom(Room room) {
        String query = "UPDATE rooms SET room_number = ?, building = ?, capacity = ?, room_type = ? WHERE room_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getBuilding());
            ps.setInt(3, room.getCapacity());
            ps.setString(4, room.getRoomType());
            ps.setString(5, room.getRoomId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRoom(String roomId) {
        String query = "DELETE FROM rooms WHERE room_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, roomId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ========== TIMETABLE MANAGEMENT ==========

    public void saveTimetable(List<TimetableEntry> timetable) {
        String deleteQuery = "DELETE FROM timetable_entries";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String query = "INSERT INTO timetable_entries (entry_id, subject_code, teacher_id, room_id, day, start_time, end_time, student_section, student_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        for (TimetableEntry entry : timetable) {
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, entry.getEntryId());
                ps.setString(2, entry.getSubject().getSubjectCode());
                ps.setString(3, entry.getTeacher().getUserId());
                ps.setString(4, entry.getRoom().getRoomId());
                ps.setString(5, entry.getTimeSlot().getDay());
                ps.setString(6, entry.getTimeSlot().getStartTime().toString());
                ps.setString(7, entry.getTimeSlot().getEndTime().toString());
                ps.setString(8, entry.getStudentSection());
                ps.setInt(9, entry.getStudentCount());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("✅ Saved " + timetable.size() + " timetable entries to database");
    }

    public List<TimetableEntry> getCurrentTimetable() {
        List<TimetableEntry> entries = new ArrayList<>();
        String query = "SELECT * FROM timetable_entries";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Subject subject = getSubject(rs.getString("subject_code"));
                Teacher teacher = getTeacher(rs.getString("teacher_id"));
                Room room = getRoom(rs.getString("room_id"));
                if (subject != null && teacher != null && room != null) {
                    TimeSlot timeSlot = new TimeSlot(
                            rs.getString("day"),
                            rs.getString("start_time"),
                            rs.getString("end_time")
                    );
                    TimetableEntry entry = new TimetableEntry(
                            subject, teacher, room, timeSlot,
                            rs.getString("student_section"),
                            rs.getInt("student_count")
                    );
                    entries.add(entry);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public List<TimetableEntry> getTimetableForTeacher(String teacherId) {
        List<TimetableEntry> entries = new ArrayList<>();
        String query = "SELECT * FROM timetable_entries WHERE teacher_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Subject subject = getSubject(rs.getString("subject_code"));
                Teacher teacher = getTeacher(rs.getString("teacher_id"));
                Room room = getRoom(rs.getString("room_id"));
                if (subject != null && teacher != null && room != null) {
                    TimeSlot timeSlot = new TimeSlot(
                            rs.getString("day"),
                            rs.getString("start_time"),
                            rs.getString("end_time")
                    );
                    TimetableEntry entry = new TimetableEntry(
                            subject, teacher, room, timeSlot,
                            rs.getString("student_section"),
                            rs.getInt("student_count")
                    );
                    entries.add(entry);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public List<TimetableEntry> getTimetableForStudent(String studentId) {
        Student student = getStudent(studentId);
        if (student == null) return new ArrayList<>();

        List<TimetableEntry> entries = new ArrayList<>();
        String query = "SELECT * FROM timetable_entries WHERE student_section = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, student.getSection());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Subject subject = getSubject(rs.getString("subject_code"));
                if (subject != null && student.getEnrolledSubjects().contains(subject.getSubjectCode())) {
                    Teacher teacher = getTeacher(rs.getString("teacher_id"));
                    Room room = getRoom(rs.getString("room_id"));
                    if (teacher != null && room != null) {
                        TimeSlot timeSlot = new TimeSlot(
                                rs.getString("day"),
                                rs.getString("start_time"),
                                rs.getString("end_time")
                        );
                        TimetableEntry entry = new TimetableEntry(
                                subject, teacher, room, timeSlot,
                                rs.getString("student_section"),
                                rs.getInt("student_count")
                        );
                        entries.add(entry);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    // ========== NOTIFICATION MANAGEMENT ==========

    public void addNotification(Notification notification) {
        String query = "INSERT INTO notifications (notification_id, user_id, title, message, type, timestamp, is_read) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, notification.getNotificationId());
            ps.setString(2, notification.getUserId());
            ps.setString(3, notification.getTitle());
            ps.setString(4, notification.getMessage());
            ps.setString(5, notification.getType());
            ps.setTimestamp(6, Timestamp.valueOf(notification.getTimestamp()));
            ps.setBoolean(7, notification.isRead());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Notification> getNotificationsForUser(String userId) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM notifications WHERE user_id = ? ORDER BY timestamp DESC";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification notification = new Notification(
                        rs.getString("user_id"),
                        rs.getString("title"),
                        rs.getString("message"),
                        rs.getString("type")
                );
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public void markNotificationRead(String notificationId) {
        String query = "UPDATE notifications SET is_read = true WHERE notification_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, notificationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ========== COMPLAINT MANAGEMENT ==========

    public void addComplaint(Complaint complaint) {
        String query = "INSERT INTO complaints (complaint_id, student_id, student_name, subject, description, status, submitted_at, admin_response) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, complaint.getComplaintId());
            ps.setString(2, complaint.getStudentId());
            ps.setString(3, complaint.getStudentName());
            ps.setString(4, complaint.getSubject());
            ps.setString(5, complaint.getDescription());
            ps.setString(6, complaint.getStatus());
            ps.setTimestamp(7, Timestamp.valueOf(complaint.getSubmittedAt()));
            ps.setString(8, complaint.getAdminResponse());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Complaint> getAllComplaints() {
        List<Complaint> complaints = new ArrayList<>();
        String query = "SELECT * FROM complaints ORDER BY submitted_at DESC";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Complaint complaint = new Complaint(
                        rs.getString("complaint_id"),
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getString("subject"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getTimestamp("submitted_at").toLocalDateTime(),
                        rs.getString("admin_response")
                );
                complaints.add(complaint);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return complaints;
    }

    public List<Complaint> getComplaintsByStudent(String studentId) {
        List<Complaint> complaints = new ArrayList<>();
        String query = "SELECT * FROM complaints WHERE student_id = ? ORDER BY submitted_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Complaint complaint = new Complaint(
                        rs.getString("complaint_id"),
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getString("subject"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getTimestamp("submitted_at").toLocalDateTime(),
                        rs.getString("admin_response")
                );
                complaints.add(complaint);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return complaints;
    }

    public List<Complaint> getPendingComplaints() {
        List<Complaint> complaints = new ArrayList<>();
        String query = "SELECT * FROM complaints WHERE status = 'PENDING' ORDER BY submitted_at DESC";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Complaint complaint = new Complaint(
                        rs.getString("complaint_id"),
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getString("subject"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getTimestamp("submitted_at").toLocalDateTime(),
                        rs.getString("admin_response")
                );
                complaints.add(complaint);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return complaints;
    }

    public void resolveComplaint(String complaintId, String response) {
        String query = "UPDATE complaints SET status = 'RESOLVED', admin_response = ? WHERE complaint_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, response);
            ps.setString(2, complaintId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        String query = "SELECT user_id FROM admins";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Admin admin = getAdminById(rs.getString("user_id"));
                if (admin != null) admins.add(admin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }
}