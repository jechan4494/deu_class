package util;

import model.student.Reservation;
import util.student.JsonDataHandler;
import org.junit.jupiter.api.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JsonDataHandlerTest {
    @Test
    void testSaveAndLoadReservations() {
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(new Reservation("1", "101", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "스터디", "student123"));
        JsonDataHandler.saveReservations(reservations, "student_test_reservations.json");
        List<Reservation> loaded = JsonDataHandler.loadReservations("student_test_reservations.json");
        assertEquals(1, loaded.size());
        assertEquals("101", loaded.get(0).getRoomNumber());
    }
} 