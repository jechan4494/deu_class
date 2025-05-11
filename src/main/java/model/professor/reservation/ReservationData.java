package model.professor.reservation;

import java.util.List;

public class ReservationData {
    private String name;        // 이름
    private String role;        // 역할 (학생,교수)
    private String roomType;    // 강의실 타입 (실습실, 일반실)
    private String roomNumber;  // 강의실 번호
    private List<String> timeSlots;  // 예약된 시간대

    public ReservationData(String name, String role, String roomType, String roomNumber, List<String> timeSlots) {
        this.name = name;
        this.role = role;
        this.roomType = roomType;
        this.roomNumber = roomNumber;
        this.timeSlots = timeSlots;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        return "ReservationData{" +
                "name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", roomType='" + roomType + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", timeSlots=" + timeSlots +
                '}';
    }
}
