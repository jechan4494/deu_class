package service.student;

import model.ta.Reservation;
import util.JsonDataHandler;
import java.util.List;
import java.util.ArrayList;

public class ReservationService {
    private final JsonDataHandler jsonHandler;

    public ReservationService() {
        this.jsonHandler = new JsonDataHandler();
    }

    public void addReservation(Reservation reservation) {
        List<Reservation> reservations = jsonHandler.loadStudentReservations();
        reservations.add(reservation);
        jsonHandler.saveStudentReservations(reservations);
    }

    public List<Reservation> getReservations() {
        return jsonHandler.loadStudentReservations();
    }

    public List<Reservation> getApprovedReservations() {
        return jsonHandler.loadStudentApprovedReservations();
    }

    public List<Reservation> getRejectedReservations() {
        return jsonHandler.loadStudentRejectedReservations();
    }

    public void approveReservation(Reservation reservation) {
        List<Reservation> reservations = getReservations();
        List<Reservation> approvedReservations = getApprovedReservations();
        
        reservations.remove(reservation);
        reservation.setState("승인됨");
        approvedReservations.add(reservation);
        
        jsonHandler.saveStudentReservations(reservations);
        jsonHandler.saveStudentApprovedReservations(approvedReservations);
    }

    public void rejectReservation(Reservation reservation) {
        List<Reservation> reservations = getReservations();
        List<Reservation> rejectedReservations = getRejectedReservations();
        
        reservations.remove(reservation);
        reservation.setState("거절됨");
        rejectedReservations.add(reservation);
        
        jsonHandler.saveStudentReservations(reservations);
        jsonHandler.saveStudentRejectedReservations(rejectedReservations);
    }
} 