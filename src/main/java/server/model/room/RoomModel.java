package server.model.room;

import server.model.user.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.io.FileInputStream; // 수정: 파일시스템에서 직접 읽기
import java.io.InputStream;
import java.util.*;

public class RoomModel {
    private JSONArray rooms;
    private JSONObject originalData;
    private Map<Integer, JSONObject> roomMap = new HashMap<>();
    private final User user = null;

    public RoomModel(String jsonPath) {
        try (InputStream inputStream = new FileInputStream(jsonPath)) {
            // 기존 코드 (JSON 파일 정상적으로 읽기)
            JSONTokener tokener = new JSONTokener(inputStream);
            Object parsed = tokener.nextValue();
            this.rooms = new JSONArray();
            this.originalData = new JSONObject();
            this.roomMap = new HashMap<>();
            if (parsed instanceof JSONArray) {
                JSONArray arr = (JSONArray) parsed;
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    if (obj.has("rooms")) {
                        JSONArray subRooms = obj.getJSONArray("rooms");
                        for (int j = 0; j < subRooms.length(); j++) {
                            JSONObject room = subRooms.getJSONObject(j);
                            this.rooms.put(room);
                            this.roomMap.put(room.getInt("roomNumber"), room);
                        }
                    }
                }
                this.originalData.put("rooms", this.rooms);
            } else {
                throw new org.json.JSONException("최상위 JSON은 반드시 배열이어야 합니다.");
            }
        } catch (java.io.FileNotFoundException fnfe) {
            // 파일이 없을 때는 안전한 초기화 & 사용자 안내
            JOptionPane.showMessageDialog(null,
                "강의실 데이터 파일을 찾을 수 없습니다: " + jsonPath + "\n" +
                "빈 강의실 데이터로 시작합니다.", "파일 없음", JOptionPane.WARNING_MESSAGE);
            this.rooms = new JSONArray();
            this.originalData = new JSONObject().put("rooms", this.rooms);
            this.roomMap = new HashMap<>();
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
                obj.put("name", "");
                obj.put("type", "");
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
    public void markCancelled(int roomNumber, String day, String time, String jsonPath) {
        JSONObject room = roomMap.get(roomNumber);
        if (room == null || !room.has("schedule")) return;
        JSONObject schedule = room.getJSONObject("schedule");
        JSONArray dayArray = schedule.optJSONArray(day);
        if (dayArray == null) return;
        for (int i = 0; i < dayArray.length(); i++) {
            JSONObject timeSlot = dayArray.getJSONObject(i);
            if (timeSlot.has("time") && timeSlot.getString("time").equals(time)) {
                timeSlot.put("state", "O");
            }
        }
    }

    public void saveToFile(String jsonPath) {
        try (java.io.FileWriter writer = new java.io.FileWriter(jsonPath, false)) {
            originalData.put("rooms", this.rooms);
            JSONArray outArr = new JSONArray();
            outArr.put(this.originalData);
            writer.write(outArr.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "파일 저장에 실패했습니다: " + jsonPath);
        }
    }
}