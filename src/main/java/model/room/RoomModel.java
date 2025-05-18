package model.room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import model.ta.Reservation;

import java.io.*;
import java.util.*;

public class RoomModel {
    private Map<Integer, JsonObject> roomMap;
    private final Gson gson;
    private final String jsonPath;

    public RoomModel(String jsonPath) {
        this.jsonPath = jsonPath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadRooms();
    }

    private void loadRooms() {
        roomMap = new HashMap<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(jsonPath)) {
            if (is == null) {
                System.err.println("리소스 파일을 찾을 수 없습니다: " + jsonPath);
                return;
            }
            try (InputStreamReader reader = new InputStreamReader(is)) {
                JsonArray rootArray = gson.fromJson(reader, JsonArray.class);
                if (rootArray != null) {
                    for (JsonElement roomElement : rootArray) {
                        JsonObject room = roomElement.getAsJsonObject();
                        if (room.has("roomNumber")) {
                            int roomNumber = room.get("roomNumber").getAsInt();
                            roomMap.put(roomNumber, room);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            roomMap = new HashMap<>();
        }
    }

    public void saveReservation(Reservation reservation) {
        JsonObject room = roomMap.get(reservation.getRoomNumber());
        if (room == null) return;

        JsonObject schedule = room.getAsJsonObject("schedule");
        if (schedule == null) {
            schedule = new JsonObject();
            room.add("schedule", schedule);
        }

        JsonArray dayArray = schedule.getAsJsonArray(reservation.getDay());
        if (dayArray == null) {
            dayArray = new JsonArray();
            schedule.add(reservation.getDay(), dayArray);
        }

        // 예약하려는 시간대가 이미 예약되어 있는지 확인
        for (String timeSlot : reservation.getTimeSlots()) {
            if (!isReservable(reservation.getRoomNumber(), reservation.getDay(), timeSlot)) {
                throw new IllegalStateException("이미 예약된 시간대가 포함되어 있습니다: " + timeSlot);
            }
        }

        // 예약 정보 저장
        for (String timeSlot : reservation.getTimeSlots()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("time", timeSlot);
            obj.addProperty("state", "X");  // 예약된 상태
            obj.addProperty("name", reservation.getName());
            obj.addProperty("role", reservation.getRole());
            obj.addProperty("type", reservation.getType());
            dayArray.add(obj);
        }

        // 파일에 저장
        File resourceFile = new File(jsonPath);
        resourceFile.getParentFile().mkdirs();
        
        try (FileWriter writer = new FileWriter(resourceFile)) {
            JsonArray rootArray = new JsonArray();
            for (JsonObject roomObj : roomMap.values()) {
                rootArray.add(roomObj);
            }
            gson.toJson(rootArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isReservable(int roomNumber, String day, String timeSlot) {
        JsonObject room = roomMap.get(roomNumber);
        if (room == null) return false;

        JsonObject schedule = room.getAsJsonObject("schedule");
        if (schedule == null) return true;

        JsonArray dayArray = schedule.getAsJsonArray(day);
        if (dayArray == null) return true;

        for (int i = 0; i < dayArray.size(); i++) {
            JsonObject slot = dayArray.get(i).getAsJsonObject();
            if (slot.get("time").getAsString().equals(timeSlot)) {
                return slot.get("state").getAsString().equals("O");
            }
        }
        return true;
    }

    public void markReserved(int roomNumber, String day, String timeSlot, String jsonPath) {
        JsonObject room = roomMap.get(roomNumber);
        if (room == null) return;

        JsonObject schedule = room.getAsJsonObject("schedule");
        if (schedule == null) {
            schedule = new JsonObject();
            room.add("schedule", schedule);
        }

        JsonArray dayArray = schedule.getAsJsonArray(day);
        if (dayArray == null) {
            dayArray = new JsonArray();
            schedule.add(day, dayArray);
        }

        // 해당 시간대 찾기
        boolean found = false;
        for (int i = 0; i < dayArray.size(); i++) {
            JsonObject slot = dayArray.get(i).getAsJsonObject();
            if (slot.get("time").getAsString().equals(timeSlot)) {
                // 기존 정보 보존하면서 상태만 변경
                slot.addProperty("state", "X");
                found = true;
                break;
            }
        }

        // 해당 시간대가 없으면 새로 추가
        if (!found) {
            JsonObject newSlot = new JsonObject();
            newSlot.addProperty("time", timeSlot);
            newSlot.addProperty("state", "X");
            newSlot.addProperty("name", "");
            newSlot.addProperty("role", "");
            newSlot.addProperty("type", "");
            dayArray.add(newSlot);
        }

        // 파일에 저장
        File resourceFile = new File(jsonPath);
        resourceFile.getParentFile().mkdirs();
        
        try (FileWriter writer = new FileWriter(resourceFile)) {
            JsonArray rootArray = new JsonArray();
            for (JsonObject roomObj : roomMap.values()) {
                rootArray.add(roomObj);
            }
            gson.toJson(rootArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getTimeSlots(int roomNumber, String day) {
        List<String> slots = new ArrayList<>();
        JsonObject room = roomMap.get(roomNumber);
        if (room == null) return slots;

        JsonObject schedule = room.getAsJsonObject("schedule");
        if (schedule == null) return slots;

        JsonArray dayArray = schedule.getAsJsonArray(day);
        if (dayArray == null) return slots;

        for (int i = 0; i < dayArray.size(); i++) {
            JsonObject slot = dayArray.get(i).getAsJsonObject();
            if (slot.get("state").getAsString().equals("O")) {  // 예약 가능한 시간대만 추가
                slots.add(slot.get("time").getAsString());
            }
        }

        return slots;
    }

    public Set<Integer> getRoomNumbers() {
        return roomMap.keySet();
    }

    public Set<String> getDays(int roomNumber) {
        JsonObject room = roomMap.get(roomNumber);
        if (room == null) return Collections.emptySet();

        JsonObject schedule = room.getAsJsonObject("schedule");
        if (schedule == null) return Collections.emptySet();

        Set<String> days = new HashSet<>();
        schedule.keySet().forEach(days::add);
        return days;
    }

    public List<String> getAvailableTimeSlots(int roomNumber, String day) {
        JsonObject room = roomMap.get(roomNumber);
        if (room == null) return Collections.emptyList();

        JsonObject schedule = room.getAsJsonObject("schedule");
        if (schedule == null) return Collections.emptyList();

        JsonArray dayArray = schedule.getAsJsonArray(day);
        if (dayArray == null) return Collections.emptyList();

        List<String> availableSlots = new ArrayList<>();
        for (int i = 0; i < dayArray.size(); i++) {
            JsonObject slot = dayArray.get(i).getAsJsonObject();
            if (slot.get("state").getAsString().equals("O")) {
                availableSlots.add(slot.get("time").getAsString());
            }
        }
        return availableSlots;
    }
}