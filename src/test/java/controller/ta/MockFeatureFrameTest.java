/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.ta;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;
import controller.ta.ReservationController;
import model.ta.Reservation;
import model.ta.ReservationModel;
import view.ta.MockFeatureFrame;

import javax.swing.table.DefaultTableModel;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MockFeatureFrameTest {

    // ✅ JSON 파일에 해당 예약 정보가 있는지 확인하는 메서드
    private boolean jsonContains(String filePath, Reservation target) {
        try (InputStream is = new FileInputStream("src/main/resources/" + filePath)) {
            JSONArray array = new JSONArray(new JSONTokener(is));
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (obj.getInt("roomNumber") == target.getRoomNumber()
                    && obj.getString("startTime").equals(target.getStartTime())
                    && obj.getString("endTime").equals(target.getEndTime())
                    && obj.getString("type").equals(target.getType())
                    && obj.getString("day").equals(target.getDay())) {
                return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Test
    void testApproveReservationAndSaveToJson() {
        ReservationModel model = new ReservationModel();
        List<Reservation> reservations = model.loadReservedReservations();

        assertFalse(reservations.isEmpty(), "예약 데이터가 없습니다. JSON 파일 확인 필요");

        Reservation testReservation = reservations.get(0);

        MockFeatureFrame view = new MockFeatureFrame();
        ReservationController controller = new ReservationController(model, view);

        // 테이블에 예약 한 건 삽입
        DefaultTableModel tableModel = (DefaultTableModel) view.getReservationTable().getModel();
        tableModel.addRow(new Object[]{
                testReservation.getRoomNumber(),
                testReservation.getStartTime(),
                testReservation.getEndTime(),
                testReservation.getType()
        });

        boolean success = controller.testSaveApprovedReservationForTest(testReservation);
        assertTrue(success, "예약 승인 저장 실패");

        boolean found = jsonContains("approved_reservations.json", testReservation);
        assertTrue(found, "승인된 예약이 JSON 파일에 저장되지 않았습니다.");
    }

    @Test
    void testRejectReservationAndSaveToJson() {
        ReservationModel model = new ReservationModel();
        List<Reservation> reservations = model.loadReservedReservations();

        assertFalse(reservations.isEmpty(), "예약 데이터가 없습니다. JSON 파일 확인 필요");

        Reservation testReservation = reservations.get(0);

        MockFeatureFrame view = new MockFeatureFrame();
        ReservationController controller = new ReservationController(model, view);

        // 테이블에 예약 한 건 삽입
        DefaultTableModel tableModel = (DefaultTableModel) view.getReservationTable().getModel();
        tableModel.addRow(new Object[]{
                testReservation.getRoomNumber(),
                testReservation.getStartTime(),
                testReservation.getEndTime(),
                testReservation.getType()
        });

        boolean success = controller.testSaveRejectedReservationForTest(testReservation);
        assertTrue(success, "예약 거절 저장 실패");

        boolean found = jsonContains("rejected_reservations.json", testReservation);
        assertTrue(found, "거절된 예약이 JSON 파일에 저장되지 않았습니다.");
    }
}