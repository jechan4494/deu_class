package server.model.student;

import java.time.LocalDateTime;

// 학생 예약 정보를 관리하는 클래스
public class Reservation {
    private String id;
    private String roomNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
    private String studentId;

    public Reservation(String id, String roomNumber, LocalDateTime startTime, LocalDateTime endTime, String purpose, String studentId) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.studentId = studentId;
    }

    public String getId() {
        return id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getStudentId() {
        return studentId;
    }
} 