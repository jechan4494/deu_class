package view.professor;

import controller.professor.ProfessorRejectedController;
import model.professor.ProfessorApprovedListModel;
import model.professor.ProfessorApprovedModel;
import model.room.RoomModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ProfessorRejectedView extends JFrame {
    private JList<String> reservationList;
    private DefaultListModel<String> listModel;
    private List<ProfessorApprovedModel> modelList;

    public ProfessorRejectedView(
            String reservationFile,
            RoomModel roomModel,
            String roomJsonPath,
            String loginUserName,
            String loginUserRole
    ) {
        setTitle("예약 취소 관리");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 데이터 로드
        try {
            ProfessorApprovedListModel reservationListModel = new ProfessorApprovedListModel(reservationFile);
            List<ProfessorApprovedModel> allReservations = reservationListModel.getList();

            // 내 예약만 추림
            modelList = allReservations.stream()
                .filter(m -> m.getRole().equals(loginUserRole) && m.getName().equals(loginUserName))
                .toList();

            listModel = new DefaultListModel<>();
            for (ProfessorApprovedModel m : modelList) {
                String info = String.format("[%d] %s, %s, %s, %s, 상태:%s",
                        m.getRoomNumber(),
                        m.getName(),
                        m.getRole(),
                        m.getDay(),
                        m.getTimeSlots().toString(),
                        m.getState()
                );
                listModel.addElement(info);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "데이터 로드 실패: " + e.getMessage());
            return;
        }

        reservationList = new JList<>(listModel);
        reservationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(reservationList);

        JButton cancelBtn = new JButton("선택 예약 취소");
        cancelBtn.addActionListener((ActionEvent evt) -> {
            int idx = reservationList.getSelectedIndex();
            if (idx == -1) {
                JOptionPane.showMessageDialog(this, "예약을 선택하세요.");
                return;
            }
            ProfessorApprovedModel selectedModel = modelList.get(idx);
            int roomNum = selectedModel.getRoomNumber();
            boolean result = ProfessorRejectedController.cancelReservation(
                    roomNum,
                    reservationFile,
                    roomModel,
                    roomJsonPath
            );
            if (result) {
                listModel.set(idx, listModel.get(idx) + " (취소됨)");
                JOptionPane.showMessageDialog(this, "예약이 취소되었습니다.");
            } else {
                JOptionPane.showMessageDialog(this, "취소 실패 - 이미 취소된 예약이거나 잘못된 접근입니다.");
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(cancelBtn);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }

    public ProfessorRejectedView(String reservationFile, String roomJsonPath_normal_rom, String roomJsonPath_lab_rom, String name, String role) {
    }
}