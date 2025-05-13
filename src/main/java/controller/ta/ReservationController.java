/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.ta;
import org.json.*;
import model.ta.Reservation;
import model.ta.ReservationModel;
import view.ta.featureFrame;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.List;

public class ReservationController {
    private ReservationModel model;
    private featureFrame view;

    public ReservationController(ReservationModel model, featureFrame view) {
        this.model = model;
        this.view = view;
    }

    public void loadReservedDataToTable() {
        List<Reservation> reservations = model.loadReservedReservations();
        DefaultTableModel tableModel = (DefaultTableModel) view.getReservationTable().getModel();
        tableModel.setRowCount(0);

        for (Reservation r : reservations) {
            Object[] row = {
                r.getRoomNumber(),
                r.getDay(),
                r.getStartTime(),
                r.getEndTime(),
                r.getType()
            };
            tableModel.addRow(row);
        }
    }

    public void approveSelectedReservation(int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "예약을 선택하세요.");
            return;
        }

        JTable table = view.getReservationTable();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        int roomNumber = (int) model.getValueAt(selectedRow, 0);
        String day = (String) model.getValueAt(selectedRow, 1);
        String startTime = (String) model.getValueAt(selectedRow, 2);
        String endTime = (String) model.getValueAt(selectedRow, 3);
        String type = (String) model.getValueAt(selectedRow, 4);

        Reservation approved = new Reservation(roomNumber, startTime, endTime, type, day);
        boolean success = saveApprovedReservation(approved);

        if (success) {
            this.model.removeReservationFromOriginalJson(approved); // ✅ 원본 JSON에서 제거
            model.removeRow(selectedRow);
            JOptionPane.showMessageDialog(view, "예약이 승인되었습니다.");
        } else {
            JOptionPane.showMessageDialog(view, "승인 중 오류가 발생했습니다.");
        }
    }

    private boolean saveApprovedReservation(Reservation reservation) {
    JSONArray data = new JSONArray();

    try (InputStream is = getClass().getClassLoader().getResourceAsStream("approved_reservations.json")) {
        if (is != null) {
            data = new JSONArray(new JSONTokener(is));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    JSONObject obj = new JSONObject();
    obj.put("roomNumber", reservation.getRoomNumber());
    obj.put("startTime", reservation.getStartTime());
    obj.put("endTime", reservation.getEndTime());
    obj.put("type", reservation.getType());
    obj.put("day", reservation.getDay());
    data.put(obj);

    try {
        File file = new File("src/main/resources/approved_reservations.json");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(data.toString(2));
            fw.flush();
        }
        return true;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    // ✅ 테스트용 메서드도 유지 (선택사항)
   public boolean testSaveApprovedReservationForTest(Reservation r) { return saveApprovedReservation(r); }
    public boolean testSaveRejectedReservationForTest(Reservation r) { return saveRejectedReservation(r); }
     
    // ✅ 예약 거절 메서드
    public void rejectSelectedReservation(int selectedRow) {
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(view, "예약을 선택하세요.");
        return;
    }

    JTable table = view.getReservationTable();
    DefaultTableModel model = (DefaultTableModel) table.getModel();

    int roomNumber = (int) model.getValueAt(selectedRow, 0);
    String day = (String) model.getValueAt(selectedRow, 1);
    String startTime = (String) model.getValueAt(selectedRow, 2);
    String endTime = (String) model.getValueAt(selectedRow, 3);
    String type = (String) model.getValueAt(selectedRow, 4);

    Reservation rejected = new Reservation(roomNumber, startTime, endTime, type, day);
    boolean success = saveRejectedReservation(rejected);

    if (success) {
        this.model.removeReservationFromOriginalJson(rejected);  // ✅ 요일 포함 객체로 삭제
        model.removeRow(selectedRow);
        JOptionPane.showMessageDialog(view, "예약이 거절되었습니다.");
    } else {
        JOptionPane.showMessageDialog(view, "거절 중 오류가 발생했습니다.");
    }
}

    private boolean saveRejectedReservation(Reservation reservation) {
    JSONArray data = new JSONArray();

    try (InputStream is = getClass().getClassLoader().getResourceAsStream("rejected_reservations.json")) {
        if (is != null) {
            data = new JSONArray(new JSONTokener(is));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    JSONObject obj = new JSONObject();
    obj.put("roomNumber", reservation.getRoomNumber());
    obj.put("startTime", reservation.getStartTime());
    obj.put("endTime", reservation.getEndTime());
    obj.put("type", reservation.getType());
    obj.put("day", reservation.getDay());  // ✅ 요일 저장 추가
    data.put(obj);

    try {
        File file = new File("src/main/resources/rejected_reservations.json");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(data.toString(2));
            fw.flush();
        }
        return true;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
    }
}

