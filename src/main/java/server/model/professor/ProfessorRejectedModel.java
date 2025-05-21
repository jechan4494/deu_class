package server.model.professor;

public class ProfessorRejectedModel {
    private int roomNumber;
    private String name;
    private String day;
    private String timeSlot;
    private String reason; // 취소 사유 등

    public ProfessorRejectedModel(int roomNumber, String name, String day, String timeSlot, String reason) {
        this.roomNumber = roomNumber;
        this.name = name;
        this.day = day;
        this.timeSlot = timeSlot;
        this.reason = reason;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getName() { return name; }
    public String getDay() { return day; }
    public String getTimeSlot() { return timeSlot; }
    public String getReason() { return reason; }
}