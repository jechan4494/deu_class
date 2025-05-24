package controller.ta;
import shared.model.ta.Reservation;
import view.ta.approvedFrame;

import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ApprovedController {

    private approvedFrame view;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ApprovedController(approvedFrame view, Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        this.view = view;
        this.socket = socket;
        this.out = out;
        this.in = in;
    }

    public void loadApprovedReservations() {
        try {
            out.writeObject("loadApproved");
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

            view.setApprovedTableModel(model);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "승인 내역 불러오기 실패");
        }
    }
}

