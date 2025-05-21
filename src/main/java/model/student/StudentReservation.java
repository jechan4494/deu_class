package model.student;

import java.util.List;

public class StudentReservation {
    private String id;              // 예약 ID
    private String userId;          // 사용자 ID
    private String userName;        // 사용자 이름
    private Integer room;           // 강의실 번호
    private String day;             // 예약 날짜
    private List<String> timeSlots; // 예약 시간대
    private String status;          // 예약 상태 (대기/승인/거절)
    private String roomType;        // 강의실 유형

    public StudentReservation(String id, String userId, String userName, Integer room, String day, List<String> timeSlots, String status, String roomType) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.room = room;
        this.day = day;
        this.timeSlots = timeSlots;
        this.status = status;
        this.roomType = roomType;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Integer getRoom() { return room; }
    public void setRoom(Integer room) { this.room = room; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public List<String> getTimeSlots() { return timeSlots; }
    public void setTimeSlots(List<String> timeSlots) { this.timeSlots = timeSlots; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
} 