  /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ta_feature.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReservationModel {

    // 예약된 강의실 데이터를 불러오는 메서드
    public List<Reservation> loadReservedReservations() {
        List<Reservation> reservations = new ArrayList<>();

        // 실습실 JSON에서 status="X"인 예약 불러오기
        reservations.addAll(readFromJson("Lab_room.json", "status", "실습실"));

        // 일반실 JSON에서 state="X"인 예약 불러오기
        reservations.addAll(readFromJson("normal_room.json", "state", "일반실"));

        return reservations;
    }
    public void removeReservationFromOriginalJson(Reservation reservation) {
        String fileName = reservation.getType().equals("실습실") ? "Lab_room.json" : "normal_room.json";
        String key = reservation.getType().equals("실습실") ? "status" : "state";

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                System.err.println("원본 파일을 찾을 수 없음: " + fileName);
                return;
            }
            JSONObject root = new JSONObject(new JSONTokener(is));
            JSONArray rooms = root.getJSONArray("rooms");

            for (int i = 0; i < rooms.length(); i++) {
                JSONObject room = rooms.getJSONObject(i);
                if (room.getInt("roomNumber") == reservation.getRoomNumber()) {
                    JSONObject schedule = room.getJSONObject("schedule");
                    for (String day : schedule.keySet()) {
                        JSONArray slots = schedule.getJSONArray(day);
                        for (int j = 0; j < slots.length(); j++) {
                            JSONObject slot = slots.getJSONObject(j);
                            String time = slot.getString("time");
                            String[] times = time.split("-");
                            if (times.length == 2 && times[0].equals(reservation.getStartTime()) && times[1].equals(reservation.getEndTime())) {
                                if (slot.getString(key).equalsIgnoreCase("X")) {
                                    slot.put(key, "O"); // 예약 해제
                                }
                            }
                        }
                    }
                }
            }

           // ✅ 원본 JSON 파일로 덮어쓰기
    File file = new File("src/main/resources/" + fileName);
    try (FileWriter fw = new FileWriter(file)) {
    fw.write(root.toString(2));
    fw.flush();
}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // JSON 파일에서 예약된 항목만 읽는 내부 메서드
    private List<Reservation> readFromJson(String fileName, String key, String type) {
        List<Reservation> list = new ArrayList<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                System.err.println(fileName + " 파일을 찾을 수 없습니다.");
                return list;
            }

            JSONObject root = new JSONObject(new JSONTokener(is));
            JSONArray rooms = root.getJSONArray("rooms");

            for (int i = 0; i < rooms.length(); i++) {
                JSONObject room = rooms.getJSONObject(i);
                int roomNumber = room.getInt("roomNumber");
                JSONObject schedule = room.getJSONObject("schedule");

                for (String day : schedule.keySet()) {
                    JSONArray slots = schedule.getJSONArray(day);
                    for (int j = 0; j < slots.length(); j++) {
                        JSONObject slot = slots.getJSONObject(j);
                        if ("X".equalsIgnoreCase(slot.getString(key))) {
                            String[] timeRange = slot.getString("time").split("-");
                            String start = timeRange[0];
                            String end = timeRange.length > 1 ? timeRange[1] : "";
                            list.add(new Reservation(roomNumber, start, end, type));
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}