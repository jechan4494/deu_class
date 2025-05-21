package model.student;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.user.User;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class StudentReservationService {
    private static final String RESERVATION_FILE = "reservations.json";
    private static final Gson gson = new Gson();

    public static List<StudentReservation> getUserReservations(User user) {
        List<StudentReservation> allReservations = loadReservations();
        if (allReservations == null) return null;

        return allReservations.stream()
                .filter(r -> r.getUserId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    public static boolean addReservation(StudentReservation reservation) {
        List<StudentReservation> reservations = loadReservations();
        if (reservations == null) {
            reservations = new ArrayList<>();
        }

        // 중복 예약 체크
        boolean isDuplicate = reservations.stream()
                .anyMatch(r -> r.getRoom().equals(reservation.getRoom()) &&
                        r.getDay().equals(reservation.getDay()) &&
                        r.getTimeSlots().stream().anyMatch(reservation.getTimeSlots()::contains));

        if (isDuplicate) {
            return false;
        }

        // 새로운 예약은 항상 '대기' 상태로 시작
        reservation.setStatus("대기");
        reservations.add(reservation);
        return saveReservations(reservations);
    }

    public static boolean approveReservation(String reservationId) {
        List<StudentReservation> reservations = loadReservations();
        if (reservations == null) return false;

        for (StudentReservation reservation : reservations) {
            if (reservation.getId().equals(reservationId)) {
                reservation.setStatus("승인");
                return saveReservations(reservations);
            }
        }
        return false;
    }

    public static boolean rejectReservation(String reservationId) {
        List<StudentReservation> reservations = loadReservations();
        if (reservations == null) return false;

        for (StudentReservation reservation : reservations) {
            if (reservation.getId().equals(reservationId)) {
                reservation.setStatus("거절");
                return saveReservations(reservations);
            }
        }
        return false;
    }

    public static List<StudentReservation> getPendingReservations() {
        List<StudentReservation> allReservations = loadReservations();
        if (allReservations == null) return null;

        return allReservations.stream()
                .filter(r -> r.getStatus().equals("대기"))
                .collect(Collectors.toList());
    }

    public static boolean cancelReservation(String reservationId) {
        List<StudentReservation> reservations = loadReservations();
        if (reservations == null) return false;

        boolean removed = reservations.removeIf(r -> r.getId().equals(reservationId));
        if (removed) {
            return saveReservations(reservations);
        }
        return false;
    }

    private static List<StudentReservation> loadReservations() {
        File file = new File(RESERVATION_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<List<StudentReservation>>(){}.getType();
            List<StudentReservation> reservations = gson.fromJson(reader, type);
            return reservations != null ? reservations : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static boolean saveReservations(List<StudentReservation> reservations) {
        try (Writer writer = new FileWriter(RESERVATION_FILE)) {
            gson.toJson(reservations, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
} 