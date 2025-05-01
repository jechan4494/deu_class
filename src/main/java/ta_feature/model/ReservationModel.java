  /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ta_feature.model;
import com.google.gson.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.*;

public class ReservationModel {
    private static final String NORMAL_ROOM_FILE = "src/main/resources/normal_room.json";
    private static final String LAB_ROOM_FILE = "src/main/resources/Lab_room.json";
    private static final String APPROVED_FILE = "src/main/resources/reservations_approved.json";
    private static final String REJECTED_FILE = "src/main/resources/reservations_rejected.json";

    public DefaultTableModel loadPendingReservations(boolean isLabRoom) {
        String path = isLabRoom ? LAB_ROOM_FILE : NORMAL_ROOM_FILE;
        return loadXReservationsFromJson(path, isLabRoom);
    }

    public DefaultTableModel loadApprovedReservations(boolean isLabRoom) {
        return loadSimpleReservationTable(APPROVED_FILE);
    }

    public DefaultTableModel loadRejectedReservations(boolean isLabRoom) {
        return loadSimpleReservationTable(REJECTED_FILE);
    }

    public void approveReservation(int roomNumber, String day, String time, boolean isLabRoom) {
        updateReservationStatus(roomNumber, day, time, isLabRoom, "승인");
        saveToJsonFile(APPROVED_FILE, roomNumber, day, time);
    }

    public void rejectReservation(int roomNumber, String day, String time, boolean isLabRoom) {
        updateReservationStatus(roomNumber, day, time, isLabRoom, "O");
        saveToJsonFile(REJECTED_FILE, roomNumber, day, time);
    }

    public void moveToRejectedFromApproved(int roomNumber, String day, String time) {
        removeFromJsonFile(APPROVED_FILE, roomNumber, day, time);
        saveToJsonFile(REJECTED_FILE, roomNumber, day, time);
    }

    public void moveToApprovedFromRejected(int roomNumber, String day, String time) {
        removeFromJsonFile(REJECTED_FILE, roomNumber, day, time);
        saveToJsonFile(APPROVED_FILE, roomNumber, day, time);
    }

    private DefaultTableModel loadXReservationsFromJson(String path, boolean isLabRoom) {
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Room", "요일", "시간", isLabRoom ? "status" : "state"}, 0
        );

        try (Reader reader = new FileReader(path)) {
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray rooms = obj.getAsJsonArray("rooms");

            for (JsonElement roomElem : rooms) {
                JsonObject roomObj = roomElem.getAsJsonObject();
                int roomNumber = roomObj.get("roomNumber").getAsInt();
                JsonObject schedule = roomObj.getAsJsonObject("schedule");

                for (String day : schedule.keySet()) {
                    JsonArray times = schedule.getAsJsonArray(day);
                    for (JsonElement timeSlot : times) {
                        JsonObject slot = timeSlot.getAsJsonObject();
                        String time = slot.get("time").getAsString();
                        String key = isLabRoom ? "status" : "state";
                        String value = slot.get(key).getAsString();
                        if ("X".equals(value)) {
                            model.addRow(new Object[]{roomNumber, day, time, value});
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return model;
    }
    public DefaultTableModel loadPendingReservationsAll() {
    DefaultTableModel mergedModel = new DefaultTableModel(
        new String[]{"Room", "요일", "시간", "구분"}, 0);

    // 일반 강의실 (normal_room.json) → "강의실"로 표시
    DefaultTableModel normalModel = loadXReservationsFromJson(NORMAL_ROOM_FILE, false);
    for (int i = 0; i < normalModel.getRowCount(); i++) {
        mergedModel.addRow(new Object[]{
            normalModel.getValueAt(i, 0),
            normalModel.getValueAt(i, 1),
            normalModel.getValueAt(i, 2),
            "강의실"
        });
    }

    // 실습실 (Lab_room.json) → "실습실"로 표시
    DefaultTableModel labModel = loadXReservationsFromJson(LAB_ROOM_FILE, true);
    for (int i = 0; i < labModel.getRowCount(); i++) {
        mergedModel.addRow(new Object[]{
            labModel.getValueAt(i, 0),
            labModel.getValueAt(i, 1),
            labModel.getValueAt(i, 2),
            "실습실"
        });
    }

    return mergedModel;
}
    
    private DefaultTableModel loadSimpleReservationTable(String path) {
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Room", "요일", "시간"}, 0
        );
        try (Reader reader = new FileReader(path)) {
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement el : array) {
                JsonObject obj = el.getAsJsonObject();
                int room = obj.get("roomNumber").getAsInt();
                String day = obj.get("day").getAsString();
                String time = obj.get("time").getAsString();
                model.addRow(new Object[]{room, day, time});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;
    }

    public void saveToJsonFile(String path, int roomNumber, String day, String time) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<JsonObject> list = new ArrayList<>();

        try {
            File file = new File(path);
            if (file.exists()) {
                JsonArray array = JsonParser.parseReader(new FileReader(file)).getAsJsonArray();
                for (JsonElement el : array) {
                    list.add(el.getAsJsonObject());
                }
            }

            JsonObject newEntry = new JsonObject();
            newEntry.addProperty("roomNumber", roomNumber);
            newEntry.addProperty("day", day);
            newEntry.addProperty("time", time);
            list.add(newEntry);

            try (Writer writer = new FileWriter(path)) {
                gson.toJson(list, writer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeFromJsonFile(String path, int roomNumber, String day, String time) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonArray newArray = new JsonArray();

        try {
            JsonArray array = JsonParser.parseReader(new FileReader(path)).getAsJsonArray();
            for (JsonElement el : array) {
                JsonObject obj = el.getAsJsonObject();
                int r = obj.get("roomNumber").getAsInt();
                String d = obj.get("day").getAsString();
                String t = obj.get("time").getAsString();
                if (r == roomNumber && d.equals(day) && t.equals(time)) continue;
                newArray.add(obj);
            }

            try (Writer writer = new FileWriter(path)) {
                gson.toJson(newArray, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateReservationStatus(int roomNumber, String day, String time,
                                         boolean isLabRoom, String newStatus) {
        String path = isLabRoom ? LAB_ROOM_FILE : NORMAL_ROOM_FILE;
        String key = isLabRoom ? "status" : "state";

        try (Reader reader = new FileReader(path)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray rooms = obj.getAsJsonArray("rooms");

            for (JsonElement roomElem : rooms) {
                JsonObject roomObj = roomElem.getAsJsonObject();
                if (roomObj.get("roomNumber").getAsInt() != roomNumber) continue;

                JsonObject schedule = roomObj.getAsJsonObject("schedule");
                JsonArray slots = schedule.getAsJsonArray(day);

                for (JsonElement slotElem : slots) {
                    JsonObject slot = slotElem.getAsJsonObject();
                    if (slot.get("time").getAsString().equals(time)) {
                        slot.addProperty(key, newStatus);
                    }
                }
            }

            try (Writer writer = new FileWriter(path)) {
                gson.toJson(obj, writer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
