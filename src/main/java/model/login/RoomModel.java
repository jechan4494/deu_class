package model.login;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

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
                System.err.println("Error: JSON file not found at path: " + jsonPath);
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
                obj.put("state", "waiting");
                obj.put("name", user.getName());
                obj.put("type", user.getRole());
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
}
