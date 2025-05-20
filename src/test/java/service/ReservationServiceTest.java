package service;

import model.student.Reservation;
import model.user.User;
import controller.student.ReservationService;
import org.junit.jupiter.api.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReservationServiceTest {
    private ReservationService service;
    private User studentUser;
    private static final String PENDING_FILE = "pending_reservations.json";
    private static final String APPROVED_FILE = "approved_reservations.json";

    @BeforeEach
    void setUp() {
        System.out.println("[setUp] 테스트 준비 시작");
        // 테스트 시작 전 파일 삭제
        new File(PENDING_FILE).delete();
        new File(APPROVED_FILE).delete();
        // 학생 역할의 User 객체 생성
        studentUser = new User("S1234", "1234", "홍길동", "컴소", "학생");
        service = new ReservationService();
        System.out.println("[setUp] 테스트 준비 완료");
    }

    @AfterEach
    void tearDown() {
        System.out.println("[tearDown] 테스트 임시 파일을 정리합니다.");
        // 테스트 종료 후 파일 삭제
        new File(PENDING_FILE).delete();
        new File(APPROVED_FILE).delete();
        System.out.println("[tearDown] 정리 완료");
    }

    @Test
    @Order(1)
    void testMakeReservation() {
        System.out.println("[testMakeReservation] 테스트 시작");
        Reservation reservation = new Reservation("911", "911", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "스터디", studentUser.getId());
        assertTrue(service.makeReservation(reservation));
        // 예약이 대기 상태로 저장되었는지 확인
        List<Reservation> pendingReservations = service.getPendingReservations();
        assertFalse(pendingReservations.isEmpty());
        assertEquals("대기", pendingReservations.get(0).getState());
        System.out.println("[testMakeReservation] 완료");
    }

    @Test
    @Order(2)
    void testGetReservationsByStudentId() {
        System.out.println("[testGetReservationsByStudentId] 시작");
        // 먼저 예약 생성
        Reservation reservation = new Reservation("915", "915", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "스터디", studentUser.getId());
        service.makeReservation(reservation);
        
        // 예약 승인
        service.approveReservation(reservation.getId());
        
        // 승인된 예약 조회
        List<Reservation> list = service.getReservationsByStudentId(studentUser.getId());
        assertFalse(list.isEmpty());
        assertEquals("승인", list.get(0).getState());
        System.out.println("[testGetReservationsByStudentId] 완료");
    }

    @Test
    @Order(3)
    void testCancelReservation() {
        System.out.println("[testCancelReservation] 시작");
        // 예약 생성 및 승인
        Reservation reservation = new Reservation("916", "916", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "스터디", studentUser.getId());
        service.makeReservation(reservation);
        service.approveReservation(reservation.getId());
        
        // 예약 취소
        assertTrue(service.cancelReservation(reservation.getId()));
        
        // 취소 후 조회
        List<Reservation> list = service.getReservationsByStudentId(studentUser.getId());
        assertTrue(list.isEmpty());
        System.out.println("[testCancelReservation] 완료");
    }
} 