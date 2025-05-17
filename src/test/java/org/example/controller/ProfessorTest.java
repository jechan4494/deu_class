package org.example.controller;

import controller.professor.ProfessorController;
import model.user.User;
import model.ta.Reservation;
import model.room.RoomModel;
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
    private static final String RESOURCES_PATH = "deu_class/src/main/resources/";
    private ProfessorController professorController;
    private ProfessorView professorView;

    @BeforeEach
    void setUp() {
        System.out.println("[setUp] 테스트 준비 시작");
        professorView = new ProfessorView();
        professorView.setRoomModel(new model.room.RoomModel(RESOURCES_PATH + "Lab_room.json"));
        var user = new User("P1234", "1234", "정찬", "컴소", "PROFESSOR");

        professorController = new ProfessorController(professorView, user);
        System.out.println("[setUp] 테스트 준비 완료");
    }

    @AfterEach
    void tearDown() {
        System.out.println("[tearDown] 테스트 임시 파일을 정리합니다.");
        List<String> testFiles = Arrays.asList(
                RESOURCES_PATH + "Lab_room.json",
                RESOURCES_PATH + "normal_room.json",
                RESOURCES_PATH + "reservations.json"
        );
        for (String fileName : testFiles) {
            try {
                Files.deleteIfExists(new File(fileName).toPath());
            } catch (Exception e) {
                System.err.println("파일 삭제 실패: " + fileName);
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
        RoomModel roomModel = professorView.getRoomModel();
        roomModel.isReservable(room, day, "09:00~09:50");
        roomModel.isReservable(room, day, "10:00~10:50");

        System.out.println("예약 가능 상태 세팅 완료");

        professorController.reserveRoom(room, day, timeSlots, roomType);

        File labRoomFile = new File(RESOURCES_PATH + "Lab_room.json");
        System.out.println("예약 파일 생성 여부: " + labRoomFile.exists());
        assertTrue(labRoomFile.exists());

        // 예약 후 상태 확인 (예약했으므로 예약 불가 상태여야 함)
        System.out.println("09:00~09:50 가능 여부: " + roomModel.isReservable(room, day, "09:00~09:50"));
        System.out.println("10:00~10:50 가능 여부: " + roomModel.isReservable(room, day, "10:00~10:50"));
        assertFalse(roomModel.isReservable(room, day, "09:00~09:50"));
        assertFalse(roomModel.isReservable(room, day, "10:00~10:50"));
        System.out.println("[testReserveRoom] 완료");
    }

    @Test
    @Order(2)
    void testSaveReservationEntry() {
        System.out.println("[testSaveReservationEntry] 시작");

        Reservation reservation = new Reservation(
                "정찬", "PROFESSOR", "실습실", 911, "월요일",
                Arrays.asList("09:00~09:50", "10:00~10:50"), "대기"
        );

        professorController.saveReservationEntry(reservation);

        File reservationFile = new File(RESOURCES_PATH + "reservations.json");
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
    void testLoadReservations() {
        System.out.println("[testLoadReservations] 시작");

        String filePath = RESOURCES_PATH + "Lab_room.json";
        String exampleJson = "[{\"roomNumber\":911,\"schedule\":{\"월요일\":[{\"time\":\"09:00~09:50\",\"state\":\"O\",\"name\":\"정찬\",\"role\":\"PROFESSOR\",\"type\":\"실습실\"}]}}]";
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(exampleJson);
            System.out.println("예시 예약 데이터 생성");
        } catch (Exception e) {
            fail("테스트 JSON 파일 생성 실패: " + e.getMessage());
        }

        List<String> availableSlots = professorView.getRoomModel().getAvailableTimeSlots(911, "월요일");

        System.out.println("사용 가능한 시간대 수: " + availableSlots.size());
        assertNotNull(availableSlots);
        assertTrue(availableSlots.contains("09:00~09:50"));

        System.out.println("[testLoadReservations] 완료");
    }
}