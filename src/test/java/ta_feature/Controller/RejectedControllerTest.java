/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ta_feature.Controller;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import ta_feature.model.ReservationModel;
import ta_feature.view.RejectedFrame;
import ta_feature.controller.RejectedController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;

public class RejectedControllerTest {

    private RejectedController controller;
    private ReservationModel model;
    private RejectedFrame view;

    private final String rejectedFile = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations_rejected.txt";
    private final String approvedFile = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations_approved.txt";

    @BeforeEach
    void setUp() {
        model = new ReservationModel();
        view = new RejectedFrame();
        controller = new RejectedController(model, view);
    }

    @Test
    void testLoadRejectedReservations() {
        controller.loadRejectedReservations();
        JTable table = view.getRejectedTable();
        assertNotNull(table);
        assertTrue(table.getRowCount() >= 0); // 비어 있어도 OK
    }

    @Test
void testApproveFromRejected() {
    String[] rowData = { "테스트", "903호", "2025-05-12", "11:00", "13:00", "거절" };
    writeLineToFile(rejectedFile, String.join(",", rowData));

    JTable mockTable = createMockTable(new String[] {
        "테스트", "903호", "2025-05-12", "11:00", "13:00", "거절"
    });

    view.setRejectedTableModel((DefaultTableModel) mockTable.getModel());
    controller.approveFromRejected(0);

    assertTrue(fileContainsPartialLine(approvedFile, "테스트,903호,2025-05-12,11:00,13:00,승인"));
}

    // ===== 헬퍼 =====
    private JTable createMockTable(String[] row) {
        String[] columns = { "이름", "강의실", "날짜", "시작 시간", "종료 시간", "상태" };
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
    private boolean fileContainsPartialLine(String filePath, String expectedSubstring) {
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains(expectedSubstring)) return true;
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return false;
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
