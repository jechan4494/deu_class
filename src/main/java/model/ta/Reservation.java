/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.ta;
public class Reservation {
    private int roomNumber;
    private String startTime;
    private String endTime;
    private String type;
    private String day;
    
    public Reservation(int roomNumber, String startTime, String endTime, String type, String day) {
        this.roomNumber = roomNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.day = day;
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
    public String getDay() {
        return day;
    }
}
