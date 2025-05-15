package org.example.controller;

import controller.professor.ProfessorReserveController;
import model.user.User;
import org.junit.jupiter.api.*;
import view.professor.ProfessorView;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProfessorTest {
    private ProfessorReserveController professorReserveController;
    private ProfessorView professorView;
    private User user; // 테스트용 교수 User

    @BeforeEach
    void setUp() {
        System.out.println("[setUp] 테스트 준비 시작");
        user = new User("P1234", "1234", "정찬", "컴소", "교수");
        professorView = new ProfessorView(user);

        professorView.roomModel = new model.room.RoomModel("lab_room.json");

        professorReserveController = new ProfessorReserveController(professorView, user);
        System.out.println("[setUp] 테스트 준비 완료");
    }

    @AfterEach
    void tearDown() {
        System.out.println("[tearDown] 테스트 임시 파일을 정리합니다.");
        List<String> testFiles = Arrays.asList(
                "src/Lab_room.json", "src/normal_room.json", "reservations.json",
                "Lab_room_Test.json", "normal_room_Test.json", "reservations_Test.json"
        );
        for (String fileName : testFiles) {
            try {
                Files.deleteIfExists(new File(fileName).toPath());
            } catch (Exception e) {
                // 무시
            }
        }
        System.out.println("[tearDown] 정리 완료");
    }

    @Test
    @Order(1)
    void testReserveRoom() {
        System.out.println("[testReserveRoom] 테스트 시작");

        int room = 911;
        String day = "화요일";
        List<String> timeSlots = Arrays.asList("09:00~09:50", "10:00~10:50");
        String roomType = "실습실";

        // RoomModel의 해당 시간대를 예약 가능한 상태로 세팅
        professorView.roomModel.isReservable(room, day, "09:00~09:50");
        professorView.roomModel.isReservable(room, day, "10:00~10:50");

        System.out.println("예약 가능 상태 세팅 완료");

        professorReserveController.reserveRoom(room, day, timeSlots, roomType);

        File labRoomFile = new File("Lab_room.json");
        System.out.println("예약 파일 생성 여부: " + labRoomFile.exists());
        assertTrue(labRoomFile.exists());

        // 예약 후 상태 확인 (예시: 예약했으므로 예약 불가 상태여야 함)
        System.out.println("09:00~09:50 가능 여부: " + professorView.roomModel.isReservable(room, day, "09:00~09:50"));
        System.out.println("10:00~10:50 가능 여부: " + professorView.roomModel.isReservable(room, day, "10:00~10:50"));
        assertFalse(professorView.roomModel.isReservable(room, day, "09:00~09:50"));
        assertFalse(professorView.roomModel.isReservable(room, day, "10:00~10:50"));
        System.out.println("[testReserveRoom] 완료");
    }

    @Test
    @Order(2)
    void testSaveReservationEntry() {
        System.out.println("[testSaveReservationEntry] 시작");

        ProfessorReserveController.ReservationEntry entry = new ProfessorReserveController.ReservationEntry(
                "정찬", "Professor", "실습실", "911", "월요일", Arrays.asList("09:00~09:50", "10:00~10:50"), "대기"
        );

        professorReserveController.saveReservationEntry(entry);

        File reservationFile = new File("reservations.json");
        System.out.println("예약 파일 생성 여부: " + reservationFile.exists());
        assertTrue(reservationFile.exists());

        try {
            String content = Files.readString(reservationFile.toPath());
            System.out.println("파일 내용: " + content);
            assertTrue(content.contains("정찬"));
            assertTrue(content.contains("월요일"));
        } catch (Exception e) {
            fail("예약 파일 읽기 실패: " + e.getMessage());
        }

        System.out.println("[testSaveReservationEntry] 완료");
    }

    @Test
    @Order(3)
    void testLoadActiveReservations() {
        System.out.println("[testLoadActiveReservations] 시작");

        String filePath = "Lab_room.json";
        String exampleJson = "[{\"name\":\"정찬\",\"role\":\"Professor\",\"roomType\":\"실습실\",\"roomNumber\":\"911\",\"day\":\"월요일\",\"timeSlots\":[\"09:00~09:50\",\"10:00~10:50\"],\"state\":\"O\"}]";
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(exampleJson);
            System.out.println("예시 예약 데이터 생성");
        } catch (Exception e) {
            fail("테스트 JSON 파일 생성 실패: " + e.getMessage());
        }

        List<ProfessorReserveController.ReservationEntry> entries = professorReserveController.loadActiveReservations(filePath);

        System.out.println("로드된 예약 수: " + entries.size());

        assertNotNull(entries);
        assertEquals(1, entries.size());
        assertEquals("정찬", entries.getFirst().name);
        assertEquals("O", entries.getFirst().state);

        System.out.println("[testLoadActiveReservations] 완료");
    }
}