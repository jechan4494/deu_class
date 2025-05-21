package controller.student;

import model.student.StudentReservation;
import model.student.StudentReservationService;
import model.user.User;
import view.student.StudentReservationView;
import view.student.StudentView;

import javax.swing.*;
import java.util.List;
import java.util.UUID;

public class StudentReserveController {
    private static final String RESERVATION_FILE = "reservations.json";

    public static void openUserReservation(User user) {
        List<StudentReservation> reservations = StudentReservationService.getUserReservations(user);
        if (reservations == null) {
            JOptionPane.showMessageDialog(null, "예약 내역을 불러오는데 실패했습니다.");
            return;
        }
        new StudentReservationView(reservations).setVisible(true);
    }

    public static boolean makeReservation(Integer room, String day, List<String> timeSlots, String roomType, User user) {
        StudentReservation reservation = new StudentReservation(
                UUID.randomUUID().toString(),  // 예약 ID 생성
                user.getId(),                  // 사용자 ID
                user.getName(),                // 사용자 이름
                room,                          // 강의실 번호
                day,                           // 예약 날짜
                timeSlots,                     // 예약 시간대
                "대기",                        // 예약 상태
                roomType                       // 강의실 유형
        );

        return StudentReservationService.addReservation(reservation);
    }
} 