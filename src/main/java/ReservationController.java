package controller;

import model.Reservation;
import service.ReservationService;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationController {
    private final ReservationService reservationService;
    
    public ReservationController() {
        this.reservationService = new ReservationService();
    }
    
    public boolean checkAvailability(String roomId, LocalDateTime start, LocalDateTime end) {
        try {
            return reservationService.isTimeSlotAvailable(roomId, start, end);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void makeReservation(Reservation reservation) {
        try {
            // 자동 승인 조건 (F-03): 관리자나 교수는 자동 승인, 학생은 승인 필요
            if (reservation.getUserId().startsWith("PROF") || reservation.getUserId().startsWith("ADMIN")) {
                reservation.setStatus(Reservation.ReservationStatus.APPROVED);
            } else {
                reservation.setStatus(Reservation.ReservationStatus.PENDING);
            }
            
            reservationService.createReservation(reservation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<Reservation> getUserReservations(String userId) {
        try {
            return reservationService.getReservationsByUser(userId);
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    // 기타 메서드 구현...
}