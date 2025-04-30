/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ta_feature.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;

public class ReservationModelTest {

    private ReservationModel model;
    private final String pendingTestFile = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations.txt";
    private final String approvedTestFile = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations_approved.txt";
    private final String rejectedTestFile = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservations_rejected.txt";
    private final String logFile = "C:\\Users\\rlarh\\OneDrive\\바탕 화면\\reservation_log.txt";

    @BeforeEach
    void setUp() {
        model = new ReservationModel();
    }

    @Test
    void testLoadPendingReservationsNotNull() {
        DefaultTableModel pending = model.loadPendingReservations();
        assertNotNull(pending);
    }

    @Test
    void testApproveReservationUpdatesApprovedFile() {
        JTable mockTable = createMockTable(new String[]{
            "김테스트", "901호", "2025-05-01", "10:00", "12:00", "대기"
        });

        writeLineToFile(pendingTestFile, "김테스트,901호,2025-05-01,10:00,12:00,대기");

        model.approveReservation(0, mockTable);

        assertTrue(fileContainsLine(approvedTestFile, "김테스트,901호,2025-05-01,10:00,12:00,승인"));
    }

    @Test
    void testRejectReservationUpdatesRejectedFile() {
        JTable mockTable = createMockTable(new String[]{
            "이테스트", "902호", "2025-05-02", "11:00", "13:00", "대기"
        });

        writeLineToFile(pendingTestFile, "이테스트,902호,2025-05-02,11:00,13:00,대기");

        model.rejectReservation(0, mockTable);

        assertTrue(fileContainsLine(rejectedTestFile, "이테스트,902호,2025-05-02,11:00,13:00,거절"));
    }

    @Test
    void testAppendLogAddsLine() {
        String before = readFile(logFile);
        model.appendLog("대기", "승인", "홍길동", "903호", "2025-05-10", "13:00", "15:00");
        String after = readFile(logFile);

        assertTrue(after.length() > before.length());
        assertTrue(after.contains("홍길동"));
    }
    @AfterEach
    void tearDown() {
    new File(rejectedTestFile).delete();  // 또는 파일 내용 비우기
    }

    // 헬퍼: 가상의 JTable 생성
    private JTable createMockTable(String[] row) {
        String[] columnNames = {"이름", "강의실", "날짜", "시작 시간", "종료 시간", "상태"};
        DefaultTableModel model = new DefaultTableModel(new String[][]{row}, columnNames);
        return new JTable(model);
    }

    // 헬퍼: 한 줄을 특정 파일에 저장
    private void writeLineToFile(String filePath, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 헬퍼: 특정 줄이 포함되어 있는지 확인
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

    // 헬퍼: 파일 내용 전체 읽기
    private String readFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
