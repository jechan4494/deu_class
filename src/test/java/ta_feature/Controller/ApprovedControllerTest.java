/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ta_feature.Controller;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import ta_feature.model.ReservationModel;
import ta_feature.view.ApprovedFrame;
import ta_feature.controller.ApprovedController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;

public class ApprovedControllerTest {

    private ApprovedController controller;
    private ReservationModel model;
    private ApprovedFrame view;

    private final String approvedFile = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations_approved.txt";
    private final String rejectedFile = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations_rejected.txt";

    @BeforeEach
    void setUp() {
        model = new ReservationModel();
        view = new ApprovedFrame();
        controller = new ApprovedController(model, view);
    }

    @Test
    void testRejectFromApproved() {
        // 테스트 데이터 작성
        String[] rowData = { "김테스트", "901호", "2025-05-01", "10:00", "12:00", "승인" };
        String line = String.join(",", rowData);
        writeLineToFile(approvedFile, line);

        JTable table = createMockTable(rowData);
        view.setApprovedTableModel((DefaultTableModel) table.getModel());

        controller.rejectFromApproved(0);

        assertTrue(fileContainsLine(rejectedFile, line.replace("승인", "거절")));
    }

    // ---------------------- 헬퍼 ----------------------

    private JTable createMockTable(String[] row) {
        String[] columns = { "이름", "강의실", "날짜", "시작 시간", "종료 시간", "상태" };
        DefaultTableModel model = new DefaultTableModel(new String[][] { row }, columns);
        return new JTable(model);
    }

    private void writeLineToFile(String path, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean fileContainsLine(String path, String expected) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
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