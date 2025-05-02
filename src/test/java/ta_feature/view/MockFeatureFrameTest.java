/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ta_feature.view;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;
import ta_feature.controller.ReservationController;
import ta_feature.model.Reservation;
import ta_feature.model.ReservationModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MockFeatureFrameTest {

    // ✅ 테스트용 Mock 뷰 (UI 없이 JTable만 제공)
   static class MockFeatureFrame extends featureFrame {
    private JTable table;

    public MockFeatureFrame() {
        table = new JTable(new DefaultTableModel(
            new Object[]{"강의실", "시작 시간", "종료 시간", "구분"}, 0
        ));
    }

    @Override
    public JTable getReservationTable() {
        return table;
    }
}

    @Test
    public void testApproveReservationAndSaveToJson() {
        // 1. 모델에서 예약 로드
        ReservationModel model = new ReservationModel();
        List<Reservation> reservations = model.loadReservedReservations();

        assertFalse(reservations.isEmpty(), "예약 데이터가 없습니다. JSON 파일 확인 필요");

        // 2. 예약 하나 선택
        Reservation testReservation = reservations.get(0);

        // 3. Mock 뷰와 컨트롤러 생성
        MockFeatureFrame view = new MockFeatureFrame();
        ReservationController controller = new ReservationController(model, view);

        // 4. 컨트롤러 내부 test용 저장 함수 실행
        boolean success = controller.testSaveApprovedReservationForTest(testReservation);
        assertTrue(success, "예약 승인 저장 실패");

        // 5. JSON에 실제로 저장됐는지 확인
        boolean found = false;
        try (Reader reader = new FileReader("approved_reservations.json")) {
            JSONArray array = new JSONArray(new JSONTokener(reader));
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (obj.getInt("roomNumber") == testReservation.getRoomNumber()
                        && obj.getString("startTime").equals(testReservation.getStartTime())
                        && obj.getString("endTime").equals(testReservation.getEndTime())
                        && obj.getString("type").equals(testReservation.getType())) {
                    found = true;
                    break;
                }
            }
        } catch (Exception e) {
            fail("JSON 파일 읽기 오류: " + e.getMessage());
        }

        assertTrue(found, "승인된 예약이 JSON 파일에 저장되지 않았습니다.");
    }
}
