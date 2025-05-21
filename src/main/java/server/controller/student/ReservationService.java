package server.controller.student;

import server.model.student.Reservation;
import server.util.student.JsonDataHandler;

import java.util.List;
import java.util.stream.Collectors;

// 학생 예약 관련 비즈니스 로직을 처리하는 서비스 클래스
public class ReservationService {
    private static final String DEFAULT_RESERVATIONS_FILE = "student_reservations.json";
    private List<Reservation> reservations;
    private String fileName;

    public ReservationService() {
        this(DEFAULT_RESERVATIONS_FILE);
    }

    public ReservationService(String fileName) {
        this.fileName = fileName;
        this.reservations = JsonDataHandler.loadReservations(fileName);
    }

    public boolean makeReservation(Reservation reservation) {
        if (isTimeSlotAvailable(reservation)) {
            reservations.add(reservation);
            JsonDataHandler.saveReservations(reservations, fileName);
            return true;
        }
        return false;
    }

    public List<Reservation> getReservationsByStudentId(String studentId) {
        return reservations.stream()
                .filter(r -> r.getStudentId() != null && r.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public List<Reservation> getReservationsByRoomNumber(String roomNumber) {
        return reservations.stream()
                .filter(r -> r.getRoomNumber().equals(roomNumber))
                .collect(Collectors.toList());
    }

    private boolean isTimeSlotAvailable(Reservation newReservation) {
        return reservations.stream()
                .noneMatch(existing -> existing.getRoomNumber().equals(newReservation.getRoomNumber()) &&
                        !(newReservation.getEndTime().isBefore(existing.getStartTime()) ||
                                newReservation.getStartTime().isAfter(existing.getEndTime())));
    }
} 