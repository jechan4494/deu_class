package model;

import java.util.List;

public class RoomReservation {
    private String roomType;
    private String day;
    private List<String> period;

    public RoomReservation(String roomType, String day, List<String> period) {
        this.roomType = roomType;
        this.day = day;
        this.period = period;
    }

    public String getRoomType() {
        return roomType;
    }
    public String getDay() {
        return day;
    }
    public List<String> getperiod() {
        return period;
    }
}
