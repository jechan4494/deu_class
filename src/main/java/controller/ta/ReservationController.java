/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.ta;
import view.ta.featureFrame;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import shared.model.ta.Reservation;

public class ReservationController {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final featureFrame view;

    public ReservationController(featureFrame view, Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        this.view = view;
        this.socket = socket;
        this.out = out;
        this.in = in;
    }

    public void loadReservedDataToTable() {
        try {
            out.writeObject("loadReserved");
            out.flush();

            Object response = in.readObject();
            if (!(response instanceof List)) {
                JOptionPane.showMessageDialog(view, "서버 응답이 잘못되었습니다.");
                return;
            }

            @SuppressWarnings("unchecked")
            List<Reservation> reservations = (List<Reservation>) response;

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"이름", "역할", "강의실 유형", "강의실 번호", "요일", "시간대", "상태"}, 0
            );

            for (Reservation r : reservations) {
                model.addRow(new Object[]{
                        r.getName(), r.getRole(), r.getType(),
                        r.getRoomNumber(), r.getDay(),
                        String.join(", ", r.getTimeSlots()), r.getState()
                });
            }

            view.setReservationTableModel(model);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "예약 목록 불러오기 실패");
        }
    }

    public void approveSelectedReservation(int selectedRow) {
        processReservation(selectedRow, "approve");
    }

    public void rejectSelectedReservation(int selectedRow) {
        processReservation(selectedRow, "reject");
    }

    private void processReservation(int selectedRow, String type) {
        JTable table = view.getReservationTable();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "예약을 선택하세요.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Reservation reservation = extractReservation(model, selectedRow);
        reservation.setType(type);

        try {
            out.writeObject(type);
            out.writeObject(reservation);
            out.flush();

            Object result = in.readObject();
            if ("success".equals(result)) {
                JOptionPane.showMessageDialog(view, "예약이 " + ("approve".equals(type) ? "승인" : "거절") + "되었습니다.");
            } else {
                JOptionPane.showMessageDialog(view, "처리에 실패했습니다.");
            }

            new Timer(200, e -> loadReservedDataToTable()).start();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "서버 통신 오류");
        }
    }

    private Reservation extractReservation(DefaultTableModel model, int row) {
        String name = (String) model.getValueAt(row, 0);
        String role = (String) model.getValueAt(row, 1);
        String roomType = (String) model.getValueAt(row, 2);
        int roomNumber = (int) model.getValueAt(row, 3);
        String day = (String) model.getValueAt(row, 4);
        String timeSlot = (String) model.getValueAt(row, 5);

        List<String> timeSlots = new ArrayList<>();
        timeSlots.add(timeSlot);

        return new Reservation(name, role, roomType, roomNumber, day, timeSlots, "대기");
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public Socket getSocket() {
        return socket;
    }
}