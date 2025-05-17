package view.ta;

import controller.ta.ApprovedController;
import model.ta.ReservationModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ApprovedFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final ApprovedController controller;
    private final JTable jTable2;
    private final JButton jButton4;
    private final JLabel jLabel1;
    private final JScrollPane jScrollPane2;

    public ApprovedFrame() {
        setTitle("승인 내역");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        jLabel1 = new JLabel("승인 내역");
        jButton4 = new JButton("이전");
        jTable2 = new JTable();
        jScrollPane2 = new JScrollPane(jTable2);

        jTable2.setModel(new DefaultTableModel(
            new Object[][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String[] {
                "이름", "역할", "강의실 유형", "강의실 번호", "요일", "시간대", "상태"
            }
        ));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(jLabel1);
        add(headerPanel, BorderLayout.NORTH);

        add(jScrollPane2, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(jButton4);
        add(buttonPanel, BorderLayout.SOUTH);

        jButton4.addActionListener(e -> {
            new FeatureFrame().setVisible(true);
            dispose();
        });

        controller = new ApprovedController(new ReservationModel(), this);
        controller.loadApprovedReservations();

        pack();
        setLocationRelativeTo(null);
    }

    public JTable getApprovedTable() {
        return jTable2;
    }

    public void setApprovedTableModel(DefaultTableModel model) {
        jTable2.setModel(model);
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
            java.util.logging.Logger.getLogger(ApprovedFrame.class.getName())
                .log(java.util.logging.Level.SEVERE, null, ex);
        }

        EventQueue.invokeLater(() -> new ApprovedFrame().setVisible(true));
    }
}