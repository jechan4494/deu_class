/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.ta;
import view.ta.rejectedFrame;
import shared.model.ta.Reservation;

import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class RejectedController {
    private rejectedFrame view;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public RejectedController(rejectedFrame view, Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        this.view = view;
        this.socket = socket;
        this.out = out;
        this.in = in;
    }

    public void loadRejectedReservations() {
        try {
            // 🔁 서버로 요청 전송
            out.writeObject("loadRejected");
            out.flush();

            Object response = in.readObject();

            if (!(response instanceof List)) {
                JOptionPane.showMessageDialog(view, "서버 응답 형식 오류");
                return;
            }

            @SuppressWarnings("unchecked")
            List<Reservation> reservations = (List<Reservation>) response;

            DefaultTableModel model = new DefaultTableModel(
                new String[]{"이름", "역할", "강의실 유형", "강의실 번호", "요일", "시간대", "상태"}, 0
            );

            for (Reservation r : reservations) {
                String firstTimeSlot = r.getTimeSlots().isEmpty() ? "" : r.getTimeSlots().get(0);
                model.addRow(new Object[]{
                        r.getName(),
                        r.getRole(),
                        r.getType(),
                        r.getRoomNumber(),
                        r.getDay(),
                        firstTimeSlot,
                        r.getState()
                });
            }

            view.setRejectedTableModel(model);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "거절 내역 불러오기 실패");
        }
    }
}

