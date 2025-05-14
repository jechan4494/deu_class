/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.ta;
import org.json.*;
import model.ta.Reservation;
import model.ta.ReservationModel;
import view.ta.featureFrame;

import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ReservationController {
    private ReservationModel model;
    private featureFrame view;
    private LogController logController;

    public ReservationController(ReservationModel model, featureFrame view) {
        this.model = model;
        this.view = view;
        this.logController = new LogController();
    }

    public void loadReservedDataToTable() {
        List<Reservation> reservations = model.loadReservedReservations();
        DefaultTableModel tableModel = new DefaultTableModel(
            new String[] {"이름", "역할", "강의실 유형", "강의실 번호", "요일", "시간대", "상태"}, 0
        );

        for (Reservation r : reservations) {
            tableModel.addRow(new Object[] {
                r.getName(),
                r.getRole(),
                r.getType(),
                r.getRoomNumber(),
                r.getDay(),
                String.join(", ", r.getTimeSlots()),
                r.getState()
            });
        }

        view.setReservationTableModel(tableModel);
    }

    public void approveSelectedReservation(int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "예약을 선택하세요.");
            return;
        }

        JTable table = view.getReservationTable();
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        String name = (String) tableModel.getValueAt(selectedRow, 0);
        String role = (String) tableModel.getValueAt(selectedRow, 1);
        String type = (String) tableModel.getValueAt(selectedRow, 2);
        int roomNumber = (int) tableModel.getValueAt(selectedRow, 3);
        String day = (String) tableModel.getValueAt(selectedRow, 4);
        String timeSlot = (String) tableModel.getValueAt(selectedRow, 5);

        List<String> timeSlots = new ArrayList<>();
        timeSlots.add(timeSlot);

        Reservation approved = new Reservation(name, role, type, roomNumber, day, timeSlots, "승인");
        boolean success = saveApprovedReservation(approved);

        if (success) {
    updateReservationState(approved, "승인");
    logController.saveTaLog("[대기->승인]", approved);  // ✅ 로그 저장
    tableModel.removeRow(selectedRow);
    JOptionPane.showMessageDialog(view, "예약이 승인되었습니다.");
} else {
            JOptionPane.showMessageDialog(view, "승인 중 오류가 발생했습니다.");
        }
    }

    public void rejectSelectedReservation(int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "예약을 선택하세요.");
            return;
        }

        JTable table = view.getReservationTable();
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        String name = (String) tableModel.getValueAt(selectedRow, 0);
        String role = (String) tableModel.getValueAt(selectedRow, 1);
        String type = (String) tableModel.getValueAt(selectedRow, 2);
        int roomNumber = (int) tableModel.getValueAt(selectedRow, 3);
        String day = (String) tableModel.getValueAt(selectedRow, 4);
        String timeSlot = (String) tableModel.getValueAt(selectedRow, 5);

        List<String> timeSlots = new ArrayList<>();
        timeSlots.add(timeSlot);

        Reservation rejected = new Reservation(name, role, type, roomNumber, day, timeSlots, "거절");
        boolean success = saveRejectedReservation(rejected);

        if (success) {
        updateReservationState(rejected, "거절");
        logController.saveTaLog("[대기->거절]", rejected);  // ✅ 로그 저장
        tableModel.removeRow(selectedRow);
        JOptionPane.showMessageDialog(view, "예약이 거절되었습니다.");
        } else {
            JOptionPane.showMessageDialog(view, "거절 중 오류가 발생했습니다.");
        }
    }

    private boolean saveApprovedReservation(Reservation reservation) {
    JSONArray data = new JSONArray();

    try (BufferedReader reader = new BufferedReader(new FileReader("approved_reservations.json"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            data.put(new JSONObject(line));
        }
    } catch (Exception e) {
        // 최초 생성 시 파일이 없을 수 있으므로 무시
    }

        String[] timeParts = reservation.getTimeSlots().get(0).split("-");
        String startTime = timeParts[0];
        String endTime = timeParts.length > 1 ? timeParts[1] : "";

        JSONObject obj = new JSONObject();
        obj.put("name", reservation.getName());
        obj.put("role", reservation.getRole());
        obj.put("roomType", reservation.getType());
        obj.put("roomNumber", reservation.getRoomNumber());
        obj.put("day", reservation.getDay());
        obj.put("timeSlots", reservation.getTimeSlots());
        obj.put("state", reservation.getState());
        data.put(obj);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter("approved_reservations.json"))) {
        for (int i = 0; i < data.length(); i++) {
            writer.write(data.getJSONObject(i).toString());
            writer.newLine();
        }
        return true;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    private boolean saveRejectedReservation(Reservation reservation) {
    JSONArray data = new JSONArray();

    try (BufferedReader reader = new BufferedReader(new FileReader("rejected_reservations.json"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            data.put(new JSONObject(line));
        }
    } catch (Exception e) {
        // 처음 실행 시 파일이 없을 수도 있음 → 무시
    }

        String[] timeParts = reservation.getTimeSlots().get(0).split("-");
        String startTime = timeParts[0];
        String endTime = timeParts.length > 1 ? timeParts[1] : "";

        JSONObject obj = new JSONObject();
        obj.put("name", reservation.getName());
        obj.put("role", reservation.getRole());
        obj.put("roomType", reservation.getType());
        obj.put("roomNumber", reservation.getRoomNumber());
        obj.put("day", reservation.getDay());
        obj.put("timeSlots", reservation.getTimeSlots());
        obj.put("state", reservation.getState());
        data.put(obj);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("rejected_reservations.json"))) {
        for (int i = 0; i < data.length(); i++) {
            writer.write(data.getJSONObject(i).toString());
            writer.newLine();
        }
        return true;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    public void updateReservationState(Reservation reservation, String newState) {
    File file = new File("reservations.json"); // 또는 위 절대 경로

    List<String> lines = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            JSONObject obj = new JSONObject(line);

            if (
                obj.getInt("roomNumber") == reservation.getRoomNumber()
                && obj.getString("day").equals(reservation.getDay())
                && obj.getJSONArray("timeSlots").length() > 0
                && obj.getJSONArray("timeSlots").getString(0).equals(reservation.getTimeSlots().get(0))
            ) {
                obj.put("state", newState); // ✅ 상태 변경
                System.out.println("✅ 상태 변경됨 → " + newState);
            }

            lines.add(obj.toString());
        }
    } catch (Exception e) {
        e.printStackTrace();
        return;
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
        for (String l : lines) {
            writer.write(l);
            writer.newLine();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}

