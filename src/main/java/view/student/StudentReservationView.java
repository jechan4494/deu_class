package view.student;

import model.student.StudentReservation;
import model.student.StudentReservationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentReservationView extends JFrame {
    private final List<StudentReservation> reservations;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public StudentReservationView(List<StudentReservation> reservations) {
        this.reservations = reservations;
        
        setTitle("나의 강의실 예약 정보");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 300);
        setLocationRelativeTo(null);

        String[] columns = {"예약 ID", "강의실 번호", "이름", "예약한 시간대", "예약 상태", "요일", "실습실/일반실"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        // 예약 데이터 표시
        for (StudentReservation r : reservations) {
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getRoom(),
                    r.getUserName(),
                    String.join(", ", r.getTimeSlots()),
                    r.getStatus(),
                    r.getDay(),
                    r.getRoomType()
            });
        }

        // 테이블 선택 모드 설정
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnCancel = new JButton("예약 취소");
        JButton btnClose = new JButton("닫기");

        btnCancel.setPreferredSize(new Dimension(100, 30));
        btnClose.setPreferredSize(new Dimension(100, 30));

        btnCancel.addActionListener(e -> cancelReservation());
        btnClose.addActionListener(e -> dispose());

        // 테이블 선택 이벤트 리스너 추가
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String status = (String) table.getValueAt(selectedRow, 4);
                    btnCancel.setEnabled(status.equals("대기") || status.equals("승인"));
                } else {
                    btnCancel.setEnabled(false);
                }
            }
        });

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnClose);

        // 레이아웃 설정
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void cancelReservation() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "취소할 예약을 선택해주세요.");
            return;
        }

        String reservationId = (String) table.getValueAt(selectedRow, 0);
        String status = (String) table.getValueAt(selectedRow, 4);
        
        if (!status.equals("대기") && !status.equals("승인")) {
            JOptionPane.showMessageDialog(this, "취소할 수 없는 예약입니다.");
            return;
        }

        StudentReservation reservation = reservations.stream()
                .filter(r -> r.getId().equals(reservationId))
                .findFirst()
                .orElse(null);

        if (reservation != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "선택한 예약을 취소하시겠습니까?",
                "예약 취소 확인",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (StudentReservationService.cancelReservation(reservationId)) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "예약이 취소되었습니다.");
                } else {
                    JOptionPane.showMessageDialog(this, "예약 취소에 실패했습니다.");
                }
            }
        }
    }
} 