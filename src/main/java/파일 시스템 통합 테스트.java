package integration;

import model.Reservation;
import org.junit.jupiter.api.*;
import service.FileService;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class FileSystemIntegrationTest {

    private static final String TEST_FILE = "test_reservations.json";
    
    @BeforeEach
    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }

    @Test
    @DisplayName("예약 데이터를 파일 시스템에 정상적으로 저장하고 읽어올 수 있어야 함")
    void reservationDataPersistenceTest() throws IOException {
        // Given
        Reservation reservation = new Reservation(
                "RES001", "ROOM101", "USER001",
                LocalDateTime.of(2023, 11, 15, 9, 0),
                LocalDateTime.of(2023, 11, 15, 10, 0),
                "수업", Reservation.ReservationStatus.APPROVED
        );

        // When
        FileService.writeData(TEST_FILE, List.of(reservation));
        List<Reservation> readReservations = FileService.readData(TEST_FILE, Reservation.class);

        // Then
        assertThat(readReservations).hasSize(1);
        assertThat(readReservations.get(0)).usingRecursiveComparison().isEqualTo(reservation);
        assertThat(Files.exists(Paths.get(TEST_FILE))).isTrue();
    }
}