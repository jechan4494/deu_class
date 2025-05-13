/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.ta;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReservationModel {

    // 예약된 강의실 데이터를 불러오는 메서드
    public List<Reservation> loadReservedReservations() {
        List<Reservation> reservations = new ArrayList<>();

        // 실습실 JSON에서 status="X"인 예약 불러오기
        reservations.addAll(readFromJson("Lab_room.json", "state", "실습실"));

        // 일반실 JSON에서 state="X"인 예약 불러오기
        reservations.addAll(readFromJson("normal_room.json", "state", "일반실"));

        return reservations;
    }
    public void removeReservationFromOriginalJson(Reservation reservation) {
    String fileName = reservation.getType().equals("실습실") ? "Lab_room.json" : "normal_room.json";

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
                if (!schedule.has(reservation.getDay())) continue;
                JSONArray slots = schedule.getJSONArray(reservation.getDay());
                for (int j = 0; j < slots.length(); j++) {
                    JSONObject slot = slots.getJSONObject(j);
                    String slotTime = slot.getString("time").trim();
                    String reservationTime = reservation.getStartTime().trim() + "-" + reservation.getEndTime().trim();

                    if (slotTime.equals(reservationTime)) {
                        if (slot.getString("state").equalsIgnoreCase("X")) {
                            slot.put("state", "O");
                        }
                        break;
                    }
                }
            }
        }

        File file = new File("src/main/resources/" + fileName);
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(root.toString(2));
            fw.flush();
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    public List<Reservation> loadApprovedReservations() {
    List<Reservation> list = new ArrayList<>();
    File file = new File("src/main/resources/approved_reservations.json");

    try (FileReader reader = new FileReader(file)) {
        JSONArray array = new JSONArray(new JSONTokener(reader));

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            int roomNumber = obj.getInt("roomNumber");
            String startTime = obj.getString("startTime");
            String endTime = obj.getString("endTime");
            String type = obj.getString("type");
            String day = obj.getString("day"); // ✅ JSON에서 day 읽기

            list.add(new Reservation(roomNumber, startTime, endTime, type, day));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
    
    public List<Reservation> loadRejectedReservations() {
    List<Reservation> list = new ArrayList<>();
    File file = new File("src/main/resources/rejected_reservations.json");

    try (FileReader reader = new FileReader(file)) {
        JSONArray array = new JSONArray(new JSONTokener(reader));

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            int roomNumber = obj.getInt("roomNumber");
            String startTime = obj.getString("startTime");
            String endTime = obj.getString("endTime");
            String type = obj.getString("type");
            String day = obj.getString("day"); // ✅ 여기 추가됨

            list.add(new Reservation(roomNumber, startTime, endTime, type, day));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    System.out.println("✅ rejected_reservations.json 항목 수: " + list.size());
    return list;
}
    // JSON 파일에서 예약된 항목만 읽는 내부 메서드
    private List<Reservation> readFromJson(String fileName, String key, String type) {
    List<Reservation> list = new ArrayList<>();
    try {
        File file = new File("src/main/resources/" + fileName);
        try (FileReader reader = new FileReader(file)) {
            JSONObject root = new JSONObject(new JSONTokener(reader));
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
                            list.add(new Reservation(roomNumber, start, end, type, day));  // ✅ 요일 포함
                        }
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
