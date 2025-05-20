package model.student;

import java.time.LocalDateTime;

/**
 * 학생 예약 정보를 관리하는 클래스
 */
public class Reservation {
    private String id;                // 예약 ID
    private String roomNumber;        // 강의실 번호
    private String studentId;         // 학생 ID
    private LocalDateTime startTime;  // 예약 시작 시간
    private LocalDateTime endTime;    // 예약 종료 시간
    private String state;            // 예약 상태 (대기, 승인, 거절)
    private String purpose;          // 예약 목적

    /**
     * 예약 객체 생성자
     * @param id 예약 ID
     * @param roomNumber 강의실 번호
     * @param startTime 예약 시작 시간
     * @param endTime 예약 종료 시간
     * @param purpose 예약 목적
     * @param studentId 학생 ID
     */
    public Reservation(String id, String roomNumber, LocalDateTime startTime, LocalDateTime endTime, String purpose, String studentId) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.studentId = studentId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.state = "대기";
        this.purpose = purpose;
    }

    // Getter와 Setter 메소드들
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
} 