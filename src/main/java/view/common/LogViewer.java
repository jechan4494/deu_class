package view.common;

import client.ReservationClient;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogViewer extends JFrame {
    private final ReservationClient client;
    private final String userType;
    private JTable logTable;
    private DefaultTableModel tableModel;
    private static final Logger LOGGER = Logger.getLogger(LogViewer.class.getName());

    public LogViewer(ReservationClient client, String userType) {
        this.client = client;
        this.userType = userType;
        initializeUI();
        loadLogs();
    }

    private void initializeUI() {
        setTitle("로그 조회");
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Create table model
        String[] columns = {"시간", "사용자", "역할", "동작", "상세 내용"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        logTable = new JTable(tableModel);
        
        // Set column widths
        logTable.getColumnModel().getColumn(0).setPreferredWidth(150); // timestamp
        logTable.getColumnModel().getColumn(1).setPreferredWidth(100); // user
        logTable.getColumnModel().getColumn(2).setPreferredWidth(100); // userType
        logTable.getColumnModel().getColumn(3).setPreferredWidth(100); // action
        logTable.getColumnModel().getColumn(4).setPreferredWidth(350); // details

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(logTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("새로고침");
        refreshButton.addActionListener(e -> loadLogs());
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadLogs() {
        try {
            tableModel.setRowCount(0);
            List<Map<String, Object>> logs = client.getLogs(userType);
            
            for (Map<String, Object> log : logs) {
                tableModel.addRow(new Object[]{
                    log.get("timestamp"),
                    log.get("user"),
                    log.get("userType"),
                    log.get("action"),
                    log.get("details")
                });
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading logs: " + e.getMessage(), e);
            System.err.println("로그 데이터 로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "로그 데이터를 불러오는 중 오류가 발생했습니다: " + e.getMessage(),
                "오류",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 