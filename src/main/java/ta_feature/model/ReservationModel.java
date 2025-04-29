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

/**
 *
 * @author rlarh
 */
public class ReservationModel {
    private static final String PENDING_FILE = "reservations.txt";
    private static final String APPROVED_FILE = "reservations_approved.txt";
    private static final String REJECTED_FILE = "reservations_rejected.txt";
    private final String pendingPath = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations.txt";
    private final String approvedPath = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations_approved.txt";
    
public void moveToRejectedFromApproved(String[] data) { //
    String approvedPath = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations_approved.txt";
    String rejectedPath = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations_rejected.txt";

    // 현재 승인 상태인 데이터를 그대로 찾기 위한 문자열
    String targetLine = String.join(",", data[0], data[1], data[2], data[3], data[4], data[5], "승인");

    List<String> lines = new ArrayList<>();

    // 승인 파일에서 해당 줄 삭제
    try (BufferedReader br = new BufferedReader(new FileReader(approvedPath))) {
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.equals(targetLine)) {
                lines.add(line); // 남길 데이터만 저장
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
        return;
    }

    // 승인 파일 덮어쓰기
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(approvedPath))) {
        for (String l : lines) {
            bw.write(l);
            bw.newLine();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // 거절 상태로 변경된 줄 추가
    String rejectedLine = String.join(",", data[0], data[1], data[2], data[3], data[4], data[5], "거절");

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(rejectedPath, true))) {
        bw.write(rejectedLine);
        bw.newLine();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public DefaultTableModel loadApprovedReservations() {
    DefaultTableModel model = new DefaultTableModel(
        new String[] {"이름", "강의실", "날짜", "시작 시간", "종료 시간", "인원 수", "상태"}, 0
    );

    String path = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations_approved.txt";

    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            if (data.length == 7 && data[6].equals("승인")) {
                model.addRow(data);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    return model;
} 
public void appendLog(String fromStatus, String toStatus,
                      String name, String room, String date,
                      String start, String end, String people) {
    String logPath = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservation_log.txt";
    String log = String.format("[%s -> %s] %s, %s, %s, %s~%s, %s명",
                               fromStatus, toStatus, name, room, date, start, end, people);

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(logPath, true))) {
        bw.write(log);
        bw.newLine();
    } catch (IOException e) {
        e.printStackTrace();
    }
} //

    // 대기 예약 불러오기
   public DefaultTableModel loadPendingReservations() {
    DefaultTableModel model = new DefaultTableModel(new String[]{
        "이름", "강의실", "날짜", "시작 시간", "종료 시간", "인원 수", "상태"
    }, 0);

    try (BufferedReader br = new BufferedReader(new FileReader(pendingPath))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split(",");
            if (tokens.length == 7 && tokens[6].trim().equals("대기")) {
                model.addRow(tokens);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    return model;
}

    // 승인 처리
    public void approveReservation(int rowIndex, JTable table) {
    String[] rowData = new String[7];
    for (int i = 0; i < 6; i++) {
        rowData[i] = table.getValueAt(rowIndex, i).toString();
    }
    rowData[6] = "대기"; // 기존 상태

    // 삭제 대상 줄
    String targetLine = String.join(",", rowData);

    List<String> lines = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(pendingPath))) {
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.equals(targetLine)) {
                lines.add(line);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(pendingPath))) {
        for (String line : lines) {
            bw.write(line);
            bw.newLine();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // 승인 파일로 이동
    rowData[6] = "승인";
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(approvedPath, true))) {
        bw.write(String.join(",", rowData));
        bw.newLine();
    } catch (IOException e) {
        e.printStackTrace();
    }

    appendLog("대기", "승인", rowData[0], rowData[1], rowData[2], rowData[3], rowData[4], rowData[5]);
}

    // 거절 처리
    public void rejectReservation(int row, JTable table) {
        moveReservation(row, table, REJECTED_FILE, "거절");
    }

    // 승인/거절 공통 처리
    private void moveReservation(int row, JTable table, String targetFile, String status) {
        String[] rowData = new String[7];
        for (int i = 0; i < 6; i++) {
            rowData[i] = table.getValueAt(row, i).toString();
        }
        rowData[6] = status;

        // 승인/거절 파일에 추가
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile, true))) {
            writer.write(String.join(",", rowData));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 대기 파일에서 삭제
        removeRowFromPendingFile(row);
    }

    // 대기 파일에서 해당 행 삭제
    private void removeRowFromPendingFile(int targetRow) {
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(PENDING_FILE))) {
            String line;
            int currentIndex = 0;
            while ((line = reader.readLine()) != null) {
                if (currentIndex != targetRow) {
                    updatedLines.add(line);
                }
                currentIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PENDING_FILE))) {
            for (String line : updatedLines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
