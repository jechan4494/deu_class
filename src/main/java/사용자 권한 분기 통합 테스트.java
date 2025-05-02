package integration;

import controller.ReservationController;
import model.Reservation;
import model.User;
import org.junit.jupiter.api.*;
import service.FileService;
import service.ReservationService;
import java.io.IOException;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

class UserRoleIntegrationTest {

    private static final String TEST_FILE = "test_reservations_role.json";
    private ReservationController reservationController;

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE));
        ReservationService reservationService = new ReservationService(new FileService(), TEST_FILE);
        reservationController = new ReservationController(reservationService);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }

    @Test
    @DisplayName("관리자 예약은 자동 승인되어야 함")
    void adminReservationShouldBeAutoApproved() throws IOException {
        // Given
        User admin = new User("ADMIN001", "password", "관리자", User.UserRole.ADMIN);
        Reservation reservation = new Reservation(
                null, "ROOM101", admin.getId(),
                LocalDateTime.of(2023, 11, 15, 9, 0),
                LocalDateTime.of(2023, 11, 15, 10, 0),
                "회의", null
        );

        // When
        reservationController.makeReservation(reservation);

        // Then
        assertThat(reservation.getStatus()).isEqualTo(Reservation.ReservationStatus.APPROVED);
    }

    @Test
    @DisplayName("학생 예약은 승인 대기 상태여야 함")
    void studentReservationShouldBePending() throws IOException {
        // Given
        User student = new User("STUD001", "password", "학생", User.UserRole.STUDENT);
        Reservation reservation = new Reservation(
                null, "ROOM101", student.getId(),
                LocalDateTime.of(2023, 11, 15, 9, 0),
                LocalDateTime.of(2023, 11, 15, 10, 0),
                "스터디", null
        );

        // When
        reservationController.makeReservation(reservation);

        // Then
        assertThat(reservation.getStatus()).isEqualTo(Reservation.ReservationStatus.PENDING);
    }
}