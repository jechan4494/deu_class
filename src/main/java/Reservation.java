package model;

import java.time.LocalDateTime;

public class Reservation {
    private String id;
    private String roomId;
    private String userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
    private ReservationStatus status;
    
    public enum ReservationStatus {
        PENDING, APPROVED, REJECTED, COMPLETED
    }
    
    // 생성자, getter, setter 생략
}