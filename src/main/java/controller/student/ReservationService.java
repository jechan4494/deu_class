package controller.student;

import model.student.Reservation;
import util.student.JsonDataHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 학생 예약 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
public class ReservationService {
    private List<Reservation> reservations;    // 모든 예약 (대기 + 승인)
    private static final String RESERVATION_FILE = "reservations.json";

    /**
     * 기본 생성자 - 예약 파일 초기화
     */
    public ReservationService() {
        this.reservations = JsonDataHandler.loadReservations(RESERVATION_FILE);
    }

    /**
     * 학생 ID로 예약 목록을 조회 (모든 상태)
     * @param studentId 학생 ID
     * @return 해당 학생의 모든 예약 목록
     */
    public List<Reservation> getReservationsByStudentId(String studentId) {
        if (studentId == null) {
            return new ArrayList<>();
        }
        return reservations.stream()
                .filter(r -> r != null && r.getStudentId() != null && 
                           r.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    /**
     * 새로운 예약 생성 (대기 상태로)
     * @param reservation 예약 정보
     * @return 예약 성공 여부
     */
    public boolean makeReservation(Reservation reservation) {
        if (!isTimeSlotAvailable(reservation)) {
            return false;
        }
        // 예약 상태를 '대기'로 설정
        reservation.setState("대기");
        reservations.add(reservation);
        return JsonDataHandler.saveReservations(reservations, RESERVATION_FILE);
    }

    /**
     * 예약 취소 (승인된 예약만)
     * @param reservationId 예약 ID
     * @return 취소 성공 여부
     */
    public boolean cancelReservation(String reservationId) {
        if (reservationId == null) {
            return false;
        }
        
        Reservation reservation = reservations.stream()
                .filter(r -> r != null && 
                           r.getId() != null && 
                           r.getId().equals(reservationId) &&
                           r.getState().equals("승인"))
                .findFirst()
                .orElse(null);
                
        if (reservation != null) {
            reservation.setState("취소");
            return JsonDataHandler.saveReservations(reservations, RESERVATION_FILE);
        }
        return false;
    }

    /**
     * 강의실 번호로 예약 목록 조회 (승인된 예약만)
     * @param roomNumber 강의실 번호
     * @return 해당 강의실의 승인된 예약 목록
     */
    public List<Reservation> getReservationsByRoomNumber(String roomNumber) {
        return reservations.stream()
                .filter(r -> r.getRoomNumber().equals(roomNumber) && r.getState().equals("승인"))
                .collect(Collectors.toList());
    }

    /**
     * 예약 시간이 중복되는지 확인 (승인된 예약과 비교)
     * @param newReservation 새로운 예약 정보
     * @return 예약 가능 여부
     */
    private boolean isTimeSlotAvailable(Reservation newReservation) {
        return reservations.stream()
                .filter(r -> r.getState().equals("승인"))
                .noneMatch(existing -> existing.getRoomNumber().equals(newReservation.getRoomNumber()) &&
                        !(newReservation.getEndTime().isBefore(existing.getStartTime()) ||
                                newReservation.getStartTime().isAfter(existing.getEndTime())));
    }

    /**
     * 대기 중인 예약을 승인 상태로 변경
     * @param reservationId 예약 ID
     * @return 승인 성공 여부
     */
    public boolean approveReservation(String reservationId) {
        Reservation reservation = reservations.stream()
                .filter(r -> r.getId().equals(reservationId) && r.getState().equals("대기"))
                .findFirst()
                .orElse(null);

        if (reservation != null) {
            reservation.setState("승인");
            return JsonDataHandler.saveReservations(reservations, RESERVATION_FILE);
        }
        return false;
    }

    /**
     * 대기 중인 예약 목록을 조회
     * @return 대기 중인 예약 목록
     */
    public List<Reservation> getPendingReservations() {
        return reservations.stream()
                .filter(r -> r.getState().equals("대기"))
                .collect(Collectors.toList());
    }

    /**
     * 승인된 예약 목록을 조회
     * @return 승인된 예약 목록
     */
    public List<Reservation> getApprovedReservations() {
        return reservations.stream()
                .filter(r -> r.getState().equals("승인"))
                .collect(Collectors.toList());
    }
} 