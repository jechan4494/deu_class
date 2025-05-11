package model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.*;

public class RoomModel {
    private JSONArray rooms;

    public RoomModel(String jsonPath) {
        try {
            // 파일을 InputStream으로 읽도록 수정
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(jsonPath);

            // JSON 파일을 읽기
            JSONTokener tokener = new JSONTokener(inputStream);
            JSONObject data = new JSONObject(tokener);
            this.rooms = data.getJSONArray("rooms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 모든 방 번호를 리스트로 반환함
    public List<Integer> getRoomNumbers() {
        List<Integer> roomNumbers = new ArrayList<>();
        // 배열을 반복하면서 방 번호를 반환
        for (int i = 0; i < rooms.length(); i++) {
            roomNumbers.add(rooms.getJSONObject(i).getInt("roomNumber"));
        }
        return roomNumbers;
    }
    // 선택된 방에 대한 선택 가능한 시간을 가져오는 메서드
    public Set<String> getDays(int roomNumber) {
        JSONObject schedule = getRoomSchedule(roomNumber);
        return schedule != null ? schedule.keySet() : new HashSet<>();
    }

    public List<String> getTimeSlots(int roomNumber, String day) {

        JSONObject schedule = getRoomSchedule(roomNumber);
        List<String> slots = new ArrayList<>();

        if (schedule != null && schedule.has(day)) {
            JSONArray day_arr = schedule.getJSONArray(day);
            for (int i = 0; i < day_arr.length(); i++) {
                slots.add(day_arr.getString(i));
            }
        }
        return slots;
    }

    private JSONObject getRoomSchedule(int roomNumber) {
        for (int i = 0; i < rooms.length(); i++) {
            JSONObject room = rooms.getJSONObject(i);
            if (room.getInt("roomNumber") == roomNumber) {
                return room.getJSONObject("schedule");
            }
        }
        return null;
    }
}
