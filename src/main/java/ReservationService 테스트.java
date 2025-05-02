package service;

import model.Reservation;
import model.Room;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationService reservationService;
    private FileService mockFileService;

    @BeforeEach
    void setUp() {
        mockFileService = mock(FileService.class);
        reservationService = new ReservationService(mockFileService);
    }

    @Test
    @DisplayName("예약 가능한 시간대 확인 - 겹치지 않는 경우")
    void isTimeSlotAvailable_shouldReturnTrueWhenNoConflict() throws IOException {
        // Given
        String roomId = "ROOM101";
        LocalDateTime start = LocalDateTime.of(2023, 11, 15, 9, 0);
        LocalDateTime end = LocalDateTime.of(2023, 11, 15, 10, 0);

        Reservation existingReservation = new Reservation(
                "RES001", roomId, "USER001",
                LocalDateTime.of(2023, 11, 15, 11, 0),
                LocalDateTime.of(2023, 11, 15, 12, 0),
                "수업", Reservation.ReservationStatus.APPROVED
        );

        when(mockFileService.readData(anyString(), eq(Reservation.class)))
                .thenReturn(List.of(existingReservation));

        // When
        boolean result = reservationService.isTimeSlotAvailable(roomId, start, end);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("예약 가능한 시간대 확인 - 겹치는 경우")
    void isTimeSlotAvailable_shouldReturnFalseWhenConflictExists() throws IOException {
        // Given
        String roomId = "ROOM101";
        LocalDateTime start = LocalDateTime.of(2023, 11, 15, 9, 30);
        LocalDateTime end = LocalDateTime.of(2023, 11, 15, 10, 30);

        Reservation existingReservation = new Reservation(
                "RES001", roomId, "USER001",
                LocalDateTime.of(2023, 11, 15, 10, 0),
                LocalDateTime.of(2023, 11, 15, 11, 0),
                "수업", Reservation.ReservationStatus.APPROVED
        );

        when(mockFileService.readData(anyString(), eq(Reservation.class)))
                .thenReturn(List.of(existingReservation));

        // When
        boolean result = reservationService.isTimeSlotAvailable(roomId, start, end);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("새 예약 생성 시 파일에 저장되어야 함")
    void createReservation_shouldSaveToFile() throws IOException {
        // Given
        Reservation newReservation = new Reservation(
                "RES002", "ROOM101", "USER002",
                LocalDateTime.of(2023, 11, 15, 14, 0),
                LocalDateTime.of(2023, 11, 15, 15, 0),
                "회의", Reservation.ReservationStatus.PENDING
        );

        when(mockFileService.readData(anyString(), eq(Reservation.class)))
                .thenReturn(List.of());
        
        // When
        reservationService.createReservation(newReservation);

        // Then
        verify(mockFileService).writeData(
                anyString(),
                argThat(list -> list.contains(newReservation))
        );
    }
}