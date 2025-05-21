package server.model.student;

// 학생 강의실 정보를 관리하는 클래스
public class Classroom {
    private String roomNumber;
    private int capacity;

    public Classroom(String roomNumber, int capacity) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }
} 