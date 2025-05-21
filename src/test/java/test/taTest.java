/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;
import model.ta.ReservationModel;
import controller.ta.LogController;
import model.ta.Reservation;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONTokener;

import static org.junit.jupiter.api.Assertions.*;

public class taTest {

    private final String approvedPath = "approved_reservations.json";
    private final String rejectedPath = "rejected_reservations.json";
    private final String logPath = "ta_log.json";
    private final String usersPath = "users.json";

    @Test
    void testLoadApprovedReservationsStateCheck() {
        ReservationModel model = new ReservationModel();
        List<Reservation> approved = model.loadApprovedReservations();
        assertNotNull(approved);
        for (Reservation r : approved) {
            assertEquals("승인", r.getState());
        }
    }

    @Test
    void testLoadRejectedReservationsStateCheck() {
        ReservationModel model = new ReservationModel();
        List<Reservation> rejected = model.loadRejectedReservations();
        assertNotNull(rejected);
        for (Reservation r : rejected) {
            assertEquals("거절", r.getState());
        }
    }

    @Test
    void testDeleteUserWithSetupAndCleanup() throws IOException {
        String testUserId = "testid";
        String testUser = """
            {
              "id": "testid",
              "password": "1234",
              "name": "테스트",
              "department": "테스트학과",
              "role": "STUDENT"
            }
        """;

        // 파일 읽기
        Path path = Path.of(usersPath);
        String content = Files.readString(path).trim();

        // testid 삽입
        if (content.endsWith("]")) {
            content = content.substring(0, content.length() - 1);
            if (!content.trim().endsWith("[")) {
                content += ",";
            }
            content += testUser + "\n]";
            Files.writeString(path, content);
        }

        // 삭제 테스트
        ReservationModel model = new ReservationModel();
        boolean deleted = model.deleteUser(testUserId);
        assertTrue(deleted, "testid 삭제 실패");

        // 삭제 확인
        String result = Files.readString(path);
        assertFalse(result.contains("\"id\": \"testid\""), "삭제 후에도 testid가 남아 있음");
    }

    @Test
    void testSaveApprovedReservationAndCheckJsonArraySize() throws IOException {
    Path path = Path.of(approvedPath);

    JSONArray beforeArr = new JSONArray(new JSONTokener(Files.newBufferedReader(path)));
    int beforeSize = beforeArr.length();

    Reservation reservation = new Reservation(
        "테스트사용자", "TA", "일반실", 999, "화요일",
        Arrays.asList("13:00-13:50"), "승인"
    );
    ReservationModel model = new ReservationModel();
    model.saveApprovedReservation(reservation);

    JSONArray afterArr = new JSONArray(new JSONTokener(Files.newBufferedReader(path)));
    int afterSize = afterArr.length();

    assertEquals(beforeSize + 1, afterSize, "승인 내역이 추가되지 않음");
}

    @Test
    void testSaveRejectedReservationAndCheckJsonArraySize() throws IOException {
    Path path = Path.of(rejectedPath);

    JSONArray beforeArr = new JSONArray(new JSONTokener(Files.newBufferedReader(path)));
    int beforeSize = beforeArr.length();

    Reservation reservation = new Reservation(
        "테스트사용자", "TA", "실습실", 998, "수요일",
        Arrays.asList("14:00-14:50"), "거절"
    );
    ReservationModel model = new ReservationModel();
    model.saveRejectedReservation(reservation);

    JSONArray afterArr = new JSONArray(new JSONTokener(Files.newBufferedReader(path)));
    int afterSize = afterArr.length();

    assertEquals(beforeSize + 1, afterSize, "거절 내역이 추가되지 않음");
}

    @Test
    void testLogAppend() throws IOException {
        long before = Files.lines(Path.of(logPath)).count();
        LogController controller = new LogController();
        controller.saveLog("TA", "테스트 로그 저장");
        long after = Files.lines(Path.of(logPath)).count();

        assertEquals(before + 1, after, "로그가 한 줄 추가되지 않음");
    }
}
