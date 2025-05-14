/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.ta;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReservationModel {

    // ✅ reservation.json에서 대기 상태만 읽도록 변경
    public List<Reservation> loadReservedReservations() {
        return readFromReservationJson("대기");
    }

    public List<Reservation> readFromReservationJson(String targetState) {
        List<Reservation> list = new ArrayList<>();
        File file = new File("reservations.json");  // 프로젝트 루트 경로 기준

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                JSONObject obj = new JSONObject(line);
                if (!obj.getString("state").equals(targetState)) continue;

                String name = obj.getString("name");
                String role = obj.getString("role");
                String type = obj.getString("roomType");
                int roomNumber = obj.getInt("roomNumber");
                String day = obj.getString("day");
                JSONArray slotArray = obj.getJSONArray("timeSlots");

                List<String> timeSlots = new ArrayList<>();
                for (int i = 0; i < slotArray.length(); i++) {
                    timeSlots.add(slotArray.getString(i));
                }

                list.add(new Reservation(name, role, type, roomNumber, day, timeSlots, targetState));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Reservation> loadApprovedReservations() {
        List<Reservation> list = new ArrayList<>();
        File file = new File("approved_reservations.json"); // ✅ 경로 수정

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            JSONObject obj = new JSONObject(line);

            String name = obj.optString("name", "");
            String role = obj.optString("role", "");
            String type = obj.getString("roomType");
            int roomNumber = obj.getInt("roomNumber");
            String day = obj.getString("day");
            JSONArray timeArray = obj.getJSONArray("timeSlots");
            List<String> timeSlots = new ArrayList<>();
            for (int i = 0; i < timeArray.length(); i++) {
                timeSlots.add(timeArray.getString(i));
            }
            String state = obj.optString("state", "승인");

            list.add(new Reservation(name, role, type, roomNumber, day, timeSlots, state));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

    public List<Reservation> loadRejectedReservations() {
        List<Reservation> list = new ArrayList<>();
        File file = new File("rejected_reservations.json"); // ✅ 경로 수정

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
    while ((line = reader.readLine()) != null) {
        JSONObject obj = new JSONObject(line);

        String name = obj.optString("name", "");
        String role = obj.optString("role", "");
        String type = obj.getString("roomType");
        int roomNumber = obj.getInt("roomNumber");
        String day = obj.getString("day");
        JSONArray timeArray = obj.getJSONArray("timeSlots");
        List<String> timeSlots = new ArrayList<>();
        for (int i = 0; i < timeArray.length(); i++) {
            timeSlots.add(timeArray.getString(i));
        }
        String state = obj.optString("state", "거절");

        list.add(new Reservation(name, role, type, roomNumber, day, timeSlots, state));
    }
    } catch (Exception e) {
    e.printStackTrace();
    }

    System.out.println("✅ rejected_reservations.json 항목 수: " + list.size());
    return list;
}
}
