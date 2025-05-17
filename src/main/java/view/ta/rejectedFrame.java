package view.ta;
import controller.ta.RejectedController;
import model.ta.ReservationModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RejectedFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;
    private RejectedController controller;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;

    public RejectedFrame() {
        initComponents();  // NetBeans 자동 생성 메서드
        controller = new RejectedController(new ReservationModel(), this);
        controller.loadRejectedReservations();  // 거절 내역 불러오기
    }

    public JTable getRejectedTable() {
        return jTable2;
    }

    public void setRejectedTableModel(DefaultTableModel model) {
        jTable2.setModel(model);
    }

    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("거절 내역");

        jButton4.setText("이전");
        jButton4.addActionListener(e -> {
            new FeatureFrame().setVisible(true);
            dispose();
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "이름", "역할", "강의실 유형", "강의실 번호", "요일", "시간대", "상태"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(246, 246, 246)
                .addComponent(jButton4)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addGap(32, 32, 32))
        );

        pack();
    }

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(RejectedFrame.class.getName())
                .log(java.util.logging.Level.SEVERE, null, ex);
        }

        EventQueue.invokeLater(() -> new RejectedFrame().setVisible(true));
    }
}