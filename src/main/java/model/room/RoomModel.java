package model.room;

import model.user.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.io.InputStream;
import java.util.*;

public class RoomModel {
    private JSONArray rooms;
    private JSONObject originalData;
    private final Map<Integer, JSONObject> roomMap = new HashMap<>();
    private final User user;

    public RoomModel(String jsonPath) {
        this.user = null; // 사용자 정보는 setUser 메서드로 따로 전달해야 함

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(jsonPath)) {
            if (inputStream == null) {
                System.err.println("Error: JSON파일이 경로를 못 찾았습니다.: " + jsonPath);
                return;
            }

            JSONTokener tokener = new JSONTokener(inputStream);
            this.originalData = new JSONObject(tokener);
            this.rooms = originalData.getJSONArray("rooms");

            for (int i = 0; i < rooms.length(); i++) {
                JSONObject room = rooms.getJSONObject(i);
                roomMap.put(room.getInt("roomNumber"), room);
            }
        } catch (Exception e) {
            e.printStackTrace();
            rooms = null;
        }
    }

    public void saveReservation(RoomReservation reservation) {
        JSONObject room = roomMap.get(reservation.getRoomNumber());
        if (room == null) return;

        JSONObject schedule = room.optJSONObject("schedule");
        if (schedule == null) {
            schedule = new JSONObject();
            room.put("schedule", schedule);
        }

        JSONArray dayArray = schedule.optJSONArray(reservation.getDay());
        if (dayArray == null) {
            dayArray = new JSONArray();
            schedule.put(reservation.getDay(), dayArray);
        }

        Set<String> already = new HashSet<>();
        for (int j = 0; j < dayArray.length(); j++) {
            JSONObject timeSlot = dayArray.getJSONObject(j);
            if (timeSlot.has("time")) {
                already.add(timeSlot.getString("time"));
            }
        }

        for (String timeSlot : reservation.getTimeSlots()) {
            if (!already.contains(timeSlot)) {
                JSONObject obj = new JSONObject();
                obj.put("time", timeSlot);
                obj.put("state", "대기");
                obj.put("name", user != null ? user.getName() : ""); // null 체크
                obj.put("type", user != null ? user.getRole() : "");
                dayArray.put(obj);
            }
        }
    }

    public List<Integer> getRoomNumbers() {
        return new ArrayList<>(roomMap.keySet());
    }

    public Set<String> getDays(int roomNumber) {
        JSONObject room = roomMap.get(roomNumber);
        Set<String> days = new HashSet<>();
        if (room != null && room.has("schedule")) {
            JSONObject schedule = room.getJSONObject("schedule");
            days.addAll(schedule.keySet());
        }
        return days;
    }

    public List<String> getTimeSlots(int roomNumber, String day) {
        JSONObject room = roomMap.get(roomNumber);
        List<String> slots = new ArrayList<>();
        if (room != null && room.has("schedule")) {
            JSONObject schedule = room.getJSONObject("schedule");
            JSONArray timeArray = schedule.optJSONArray(day);
            if (timeArray != null) {
                for (int i = 0; i < timeArray.length(); i++) {
                    JSONObject obj = timeArray.getJSONObject(i);
                    if (obj.has("time")) {
                        slots.add(obj.getString("time"));
                    }
                }
            }
        }
        return slots;
    }

    // 예약 가능 여부 확인
    public boolean isReservable(int roomNumber, String day, String time) {
        JSONObject room = roomMap.get(roomNumber);
        if (room == null || !room.has("schedule")) return false;
        JSONObject schedule = room.getJSONObject("schedule");
        JSONArray dayArray = schedule.optJSONArray(day);
        if (dayArray == null) return false;
        for (int i = 0; i < dayArray.length(); i++) {
            JSONObject timeSlot = dayArray.getJSONObject(i);
            if (timeSlot.has("time") && timeSlot.has("state")) {
                if (timeSlot.getString("time").equals(time) && !"O".equals(timeSlot.getString("state"))) {
                    // 이미 예약된 상태("O"가 아님)
                    return false;
                }
            }
        }
        // 아직 비어있거나, 모두 "O"라면 예약 가능
        return true;
    }

    // 해당 시간의 state를 "X"로 변경한 뒤 파일에 저장
    public void markReserved(int roomNumber, String day, String time, String jsonPath) {
        JSONObject room = roomMap.get(roomNumber);
        if (room == null || !room.has("schedule")) return;
        JSONObject schedule = room.getJSONObject("schedule");
        JSONArray dayArray = schedule.optJSONArray(day);
        if (dayArray == null) return;
        for (int i = 0; i < dayArray.length(); i++) {
            JSONObject timeSlot = dayArray.getJSONObject(i);
            if (timeSlot.has("time") && timeSlot.getString("time").equals(time)) {
                timeSlot.put("state", "X");
                break;
            }
        }
        saveToFile(jsonPath); // 변경 후 파일에 저장
    }

    public void saveToFile(String jsonPath) {
        try (java.io.FileWriter writer = new java.io.FileWriter(jsonPath, false)) {
            writer.write(originalData.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "파일 저장에 실패했습니다: " + jsonPath);
        }
    }
}