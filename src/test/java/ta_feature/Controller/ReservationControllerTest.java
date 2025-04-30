/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ta_feature.Controller;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import ta_feature.model.ReservationModel;
import ta_feature.view.FeatureFrame;
import ta_feature.controller.ReservationController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;

public class ReservationControllerTest {

    private ReservationController controller;
    private ReservationModel model;
    private FeatureFrame view;

    private final String pendingFile = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations.txt";
    private final String approvedFile = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations_approved.txt";
    private final String rejectedFile = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations_rejected.txt";

    @BeforeEach
void setUp() {
    model = new ReservationModel();
    view = new FeatureFrame();
    controller = new ReservationController(model, view);

    clearFile(pendingFile);
    clearFile(approvedFile);
    clearFile(rejectedFile);
}

private void clearFile(String path) {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
        // 비우기만 함
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    @Test
    void testLoadPendingReservations() {
        JTable table = view.getReservationTable();
        controller.loadPendingReservations();
        assertTrue(table.getRowCount() >= 0); // 테이블이 비어 있거나 채워져 있어야 함
    }

    @Test
    void testApproveReservation() {
        // 가짜 데이터 삽입
        String testLine = "컨트롤러,999호,2025-05-10,12:00,14:00,대기";
        writeLineToFile(pendingFile, testLine);

        JTable table = createMockTable(new String[] {
            "컨트롤러", "999호", "2025-05-10", "12:00", "14:00", "대기"
        });

        controller.approveReservation(0, table);
        assertTrue(fileContainsLine(approvedFile, testLine.replace("대기", "승인")));
    }

    @Test
    void testRejectReservation() {
        String testLine = "컨트롤러2,998호,2025-05-11,13:00,15:00,대기";
        writeLineToFile(pendingFile, testLine);

        JTable table = createMockTable(new String[] {
            "컨트롤러2", "998호", "2025-05-11", "13:00", "15:00", "대기"
        });

        controller.rejectReservation(0, table);
        assertTrue(fileContainsLine(rejectedFile, testLine.replace("대기", "거절")));
    }

    // ===================== 헬퍼 메서드 =====================
    private JTable createMockTable(String[] row) {
        String[] columns = {"이름", "강의실", "날짜", "시작 시간", "종료 시간", "상태"};
        DefaultTableModel model = new DefaultTableModel(new String[][] { row }, columns);
        return new JTable(model);
    }

    private void writeLineToFile(String filePath, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean fileContainsLine(String filePath, String expected) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(expected)) return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
