package controller;

import model.Reservation;
import model.User;
import org.junit.jupiter.api.*;
import org.mockito.*;
import service.ReservationService;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;
    
    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("교수 예약은 자동 승인되어야 함")
    void makeReservation_shouldAutoApproveForProfessor() {
        // Given
        User professor = new User("PROF001", "password", "김교수", User.UserRole.PROFESSOR);
        Reservation reservation = new Reservation(
                null, "ROOM101", professor.getId(),
                LocalDateTime.of(2023, 11, 15, 9, 0),
                LocalDateTime.of(2023, 11, 15, 10, 0),
                "수업", null
        );

        when(reservationService.isTimeSlotAvailable(anyString(), any(), any()))
                .thenReturn(true);

        // When
        reservationController.makeReservation(reservation);

        // Then
        assertThat(reservation.getStatus()).isEqualTo(Reservation.ReservationStatus.APPROVED);
    }

    @Test
    @DisplayName("학생 예약은 승인 대기 상태여야 함")
    void makeReservation_shouldSetPendingForStudent() {
        // Given
        User student = new User("STUD001", "password", "이학생", User.UserRole.STUDENT);
        Reservation reservation = new Reservation(
                null, "ROOM101", student.getId(),
                LocalDateTime.of(2023, 11, 15, 9, 0),
                LocalDateTime.of(2023, 11, 15, 10, 0),
                "스터디", null
        );

        when(reservationService.isTimeSlotAvailable(anyString(), any(), any()))
                .thenReturn(true);

        // When
        reservationController.makeReservation(reservation);

        // Then
        assertThat(reservation.getStatus()).isEqualTo(Reservation.ReservationStatus.PENDING);
    }

    @Test
    @DisplayName("시간대가 겹치면 예약 실패해야 함")
    void makeReservation_shouldFailWhenTimeSlotNotAvailable() {
        // Given
        User user = new User("USER001", "password", "사용자", User.UserRole.STUDENT);
        Reservation reservation = new Reservation(
                null, "ROOM101", user.getId(),
                LocalDateTime.of(2023, 11, 15, 9, 0),
                LocalDateTime.of(2023, 11, 15, 10, 0),
                "스터디", null
        );

        when(reservationService.isTimeSlotAvailable(anyString(), any(), any()))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> reservationController.makeReservation(reservation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 시간대에 예약이 이미 존재합니다");
    }
}