package test.service;

import model.student.StudentReservation;
import model.student.StudentReservationService;
import model.ta.Reservation;
import model.user.User;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReservationServiceTest {
    private StudentReservationService service;
    private User studentUser;
    private static final String TEST_FILE = "student_test_reservations.json";

    @BeforeEach
    void setUp() {
        System.out.println("[setUp] 테스트 준비 시작");
        // 테스트 시작 전 파일 삭제
        new File(TEST_FILE).delete();
        // 학생 역할의 User 객체 생성
        studentUser = new User("S1234", "1234", "홍길동", "컴소", "학생");
        service = new StudentReservationService();
        System.out.println("[setUp] 테스트 준비 완료");
    }

    @AfterEach
    void tearDown() {
        System.out.println("[tearDown] 테스트 임시 파일을 정리합니다.");
        // 테스트 종료 후 파일 삭제
        new File(TEST_FILE).delete();
        System.out.println("[tearDown] 정리 완료");
    }

    @Test
    @Order(1)
    void testMakeReservation() {
        System.out.println("[testMakeReservation] 테스트 시작");
        StudentReservation reservation = new StudentReservation("911", "911", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "스터디", studentUser.getId());
        assertTrue(service.makeReservation(reservation));
        System.out.println("[testMakeReservation] 완료");
    }

    @Test
    @Order(2)
    void testGetReservationsByStudentId() {
        System.out.println("[testGetReservationsByStudentId] 시작");
        StudentReservation reservation = new StudentReservation("915", "915", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "스터디", studentUser.getId());
        service.makeReservation(reservation);
        List<Reservation> list = service.getReservationsByStudentId(studentUser.getId());
        assertFalse(list.isEmpty());
        System.out.println("[testGetReservationsByStudentId] 완료");
    }
}