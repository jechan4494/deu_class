/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.ta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.ta.Reservation;
import model.ta.ReservationModel;
import view.ta.featureFrame;

import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;

public class ReservationController {
    private static final String RESERVATIONS_PATH = "deu_class/src/main/resources/reservations.json";
    private static final String APPROVED_RESERVATIONS_PATH = "deu_class/src/main/resources/approved_reservations.json";
    private static final String REJECTED_RESERVATIONS_PATH = "deu_class/src/main/resources/rejected_reservations.json";
    private static final Type RESERVATION_LIST_TYPE = new TypeToken<List<Reservation>>(){}.getType();

    private final ReservationModel model;
    private final featureFrame view;
    private final LogController logController;
    private final Gson gson;

    public ReservationController(ReservationModel model, featureFrame view) {
        this.model = model;
        this.view = view;
        this.logController = new LogController();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadReservedDataToTable();
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
        boolean success = saveReservation(approved, APPROVED_RESERVATIONS_PATH);
        
        if (success) {
            // 기존 예약 목록에서 제거
            tableModel.removeRow(selectedRow);
            
            // 상태 업데이트
            updateReservationState(approved, "승인");
            
            // 로그 기록
            logController.saveTaLog("[대기->승인]", approved);
            
            JOptionPane.showMessageDialog(view, "예약이 승인되었습니다.");
        } else {
            JOptionPane.showMessageDialog(view, "예약 승인 중 오류가 발생했습니다.");
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
        boolean success = saveReservation(rejected, REJECTED_RESERVATIONS_PATH);

        if (success) {
            updateReservationState(rejected, "거절");
            logController.saveTaLog("[대기->거절]", rejected);
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(view, "예약이 거절되었습니다.");
        } else {
            JOptionPane.showMessageDialog(view, "거절 중 오류가 발생했습니다.");
        }
    }

    private boolean saveReservation(Reservation reservation, String filePath) {
        List<Reservation> reservations = new ArrayList<>();
        File file = new File(filePath);

        // 기존 데이터 읽어오기
        if (file.exists() && file.length() > 0) {
            try (FileReader reader = new FileReader(file)) {
                reservations = gson.fromJson(reader, RESERVATION_LIST_TYPE);
                if (reservations == null) {
                    reservations = new ArrayList<>();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 새 예약 추가
        reservations.add(reservation);

        // 파일에 저장
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(reservations, writer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateReservationState(Reservation reservation, String newState) {
        File file = new File(RESERVATIONS_PATH);
        List<Reservation> reservations = new ArrayList<>();

        try (FileReader reader = new FileReader(file)) {
            reservations = gson.fromJson(reader, RESERVATION_LIST_TYPE);

            if (reservations != null) {
                for (Reservation r : reservations) {
                    if (r.getRoomNumber() == reservation.getRoomNumber()
                        && r.getDay().equals(reservation.getDay())
                        && r.getTimeSlots().equals(reservation.getTimeSlots())) {
                        r.setState(newState);
                        System.out.println("✅ 상태 변경됨 → " + newState);
                        break;
                    }
                }

                try (FileWriter writer = new FileWriter(file, false)) {
                    gson.toJson(reservations, writer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

