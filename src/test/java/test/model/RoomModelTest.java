package test.model;

import server.model.room.RoomModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

class RoomModelTest {
    private static Path tempJson;
    private RoomModel roomModel;

    @BeforeAll
    static void setUpFile() throws Exception {
        // 테스트용 샘플 데이터 작성
        JSONObject sampleRoom = new JSONObject()
                .put("roomNumber", 101)
                .put("schedule", new JSONObject()
                        .put("월요일", new JSONArray()
                                .put(new JSONObject()
                                        .put("time", "09:00")
                                        .put("state", "O"))));
        JSONArray arr = new JSONArray().put(new JSONObject().put("rooms", new JSONArray().put(sampleRoom)));
        tempJson = Files.createTempFile("roomtest", ".json");
        Files.writeString(tempJson, arr.toString());
    }

    @AfterAll
    static void cleanUpFile() throws Exception {
        Files.deleteIfExists(tempJson);
    }

    @BeforeEach
    void initModel() {
        roomModel = new RoomModel(tempJson.toString());
    }

    @Test
    void getRoomNumbers_테스트() {
        List<Integer> numbers = roomModel.getRoomNumbers();
        Assertions.assertTrue(numbers.contains(101));
        System.out.println("getRoomNumbers_테스트 완료");
    }

    @Test
    void getDays_테스트() {
        Set<String> days = roomModel.getDays(101);
        Assertions.assertTrue(days.contains("월요일"));
        System.out.println("getDays_테스트 완료");
    }

    @Test
    void getTimeSlots_테스트() {
        List<String> slots = roomModel.getTimeSlots(101, "월요일");
        Assertions.assertTrue(slots.contains("09:00"));
        System.out.println("getTimeSlots_테스트 완료");
    }

    @Test
    void isReservable_테스트() {
        Assertions.assertTrue(roomModel.isReservable(101, "월요일", "09:00"));
        System.out.println("isReservable_테스트 완료");
    }

    // RoomReservation 목클래스 사용 (간단화 ver)
    static class RoomReservation {
        private final int roomNumber;
        private final String day;
        private final Set<String> timeSlots;
        RoomReservation(int roomNumber, String day, Set<String> timeSlots) {
            this.roomNumber = roomNumber;
            this.day = day;
            this.timeSlots = timeSlots;
        }
        int getRoomNumber() { return roomNumber; }
        String getDay() { return day; }
        Set<String> getTimeSlots() { return timeSlots; }
    }

    @Test
    void saveReservation_테스트() {
        server.model.room.RoomReservation reservation = new server.model.room.RoomReservation(
                101, "월요일", List.of("10:00"), "실습실", "대기", "정찬", "교수");
        roomModel.saveReservation(reservation);
        List<String> result = roomModel.getTimeSlots(101, "월요일");
        Assertions.assertTrue(result.contains("10:00"));
        System.out.println("saveReservation_테스트 완료");
    }

    @Test
    void markReserved_테스트() {
        roomModel.markReserved(101, "월요일", "09:00", tempJson.toString());
        // 저장 후 다시 읽어 확인
        RoomModel reload = new RoomModel(tempJson.toString());
        List<String> slots = reload.getTimeSlots(101, "월요일");
        Assertions.assertTrue(slots.contains("09:00"));
        System.out.println("markReserved_테스트 완료");
    }
}