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
    File file = new File("reservations.json");

    try (FileReader reader = new FileReader(file)) {
        JSONArray arr = new JSONArray(new JSONTokener(reader));

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);

            if (!obj.getString("state").equals(targetState)) continue;

            String name = obj.getString("name");
            String role = obj.getString("role");
            String type = obj.getString("roomType");
            int roomNumber = Integer.parseInt(obj.getString("roomNumber"));
            String day = obj.getString("day");

            JSONArray slotArray = obj.getJSONArray("timeSlots");
            List<String> timeSlots = new ArrayList<>();
            for (int j = 0; j < slotArray.length(); j++) {
                timeSlots.add(slotArray.getString(j));
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
    File file = new File("approved_reservations.json");

    try (FileReader reader = new FileReader(file)) {
        JSONArray arr = new JSONArray(new JSONTokener(reader));

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);

            String name = obj.optString("name", "");
            String role = obj.optString("role", "");
            String type = obj.getString("roomType");
            int roomNumber = obj.getInt("roomNumber");
            String day = obj.getString("day");

            JSONArray timeArray = obj.getJSONArray("timeSlots");
            List<String> timeSlots = new ArrayList<>();
            for (int j = 0; j < timeArray.length(); j++) {
                timeSlots.add(timeArray.getString(j));
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
    File file = new File("rejected_reservations.json");

    try (FileReader reader = new FileReader(file)) {
        JSONArray arr = new JSONArray(new JSONTokener(reader));

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);

            String name = obj.optString("name", "");
            String role = obj.optString("role", "");
            String type = obj.getString("roomType");
            int roomNumber = obj.getInt("roomNumber");
            String day = obj.getString("day");

            JSONArray timeArray = obj.getJSONArray("timeSlots");
            List<String> timeSlots = new ArrayList<>();
            for (int j = 0; j < timeArray.length(); j++) {
                timeSlots.add(timeArray.getString(j));
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
