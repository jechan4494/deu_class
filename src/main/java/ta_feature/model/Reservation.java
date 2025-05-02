/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ta_feature.model;

/**
 *
 * @author rlarh
 */
public class Reservation {
    private int roomNumber;
    private String startTime;
    private String endTime;
    private String type;

    public Reservation(int roomNumber, String startTime, String endTime, String type) {
        this.roomNumber = roomNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getType() {
        return type;
    }
}
