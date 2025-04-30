/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ta_feature.model;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationModel {
    private static final String PENDING_FILE = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations.txt";
    private static final String APPROVED_FILE = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations_approved.txt";
    private static final String REJECTED_FILE = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations_rejected.txt";
    private static final String LOG_FILE = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservation_log.txt";

    // ✅ 로그 남기기 (인원 수 제거)
    public void appendLog(String fromStatus, String toStatus,
                          String name, String room, String date,
                          String start, String end) {
        String log = String.format("[%s -> %s] %s, %s, %s, %s~%s",
                                   fromStatus, toStatus, name, room, date, start, end);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            bw.write(log);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DefaultTableModel loadPendingReservations() {
        return loadReservationsFromFile(PENDING_FILE, "대기");
    }

    public DefaultTableModel loadApprovedReservations() {
        return loadReservationsFromFile(APPROVED_FILE, "승인");
    }

    public DefaultTableModel loadRejectedReservations() {
        return loadReservationsFromFile(REJECTED_FILE, "거절");
    }

    // ✅ 공통 로딩 로직 (6컬럼 기준)
    private DefaultTableModel loadReservationsFromFile(String path, String statusFilter) {
        DefaultTableModel model = new DefaultTableModel(
            new String[] { "이름", "강의실", "날짜", "시작 시간", "종료 시간", "상태" }, 0);
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length == 6 && tokens[5].equals(statusFilter)) {
    model.addRow(new String[]{
        tokens[0], tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]
    });
}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;
    }

    public void approveReservation(int rowIndex, JTable table) {
        String[] rowData = getRowData(table, rowIndex, "대기");
        removeLineFromFile(PENDING_FILE, String.join(",", rowData));
        rowData[5] = "승인";
        appendToFile(APPROVED_FILE, rowData);
        appendLog("대기", "승인", rowData[0], rowData[1], rowData[2], rowData[3], rowData[4]);
    }

    public void rejectReservation(int rowIndex, JTable table) {
        String[] rowData = getRowData(table, rowIndex, "대기");
        removeLineFromFile(PENDING_FILE, String.join(",", rowData));
        rowData[5] = "거절";
        appendToFile(REJECTED_FILE, rowData);
        appendLog("대기", "거절", rowData[0], rowData[1], rowData[2], rowData[3], rowData[4]);
    }

    public void moveToRejectedFromApproved(String[] data) {
        moveReservationLine(data, "승인", "거절", APPROVED_FILE, REJECTED_FILE);
    }

    public void moveToApprovedFromRejected(String[] data) {
        moveReservationLine(data, "거절", "승인", REJECTED_FILE, APPROVED_FILE);
    }

    // ✅ 상태 이동 공통 처리
    private void moveReservationLine(String[] data, String fromStatus, String toStatus,
                                     String fromPath, String toPath) {
        String targetLine = String.join(",", data[0], data[1], data[2], data[3], data[4], fromStatus);
        removeLineFromFile(fromPath, targetLine);

        String newLine = String.join(",", data[0], data[1], data[2], data[3], data[4], toStatus);
        appendToFile(toPath, newLine.split(","));
        appendLog(fromStatus, toStatus, data[0], data[1], data[2], data[3], data[4]);
    }

    // ✅ 테이블에서 6개 필드 추출
    private String[] getRowData(JTable table, int rowIndex, String status) {
        String[] rowData = new String[6];
        for (int i = 0; i < 5; i++) {
            rowData[i] = table.getValueAt(rowIndex, i).toString();
        }
        rowData[5] = status;
        return rowData;
    }

    private void removeLineFromFile(String filePath, String targetLine) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equals(targetLine)) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendToFile(String filePath, String[] rowData) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(String.join(",", rowData));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
