/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ta_feature.model;

import model.ta.Reservation;
import model.ta.ReservationModel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationModelTest {

    @Test
    void testLoadReservedReservations() {
        ReservationModel model = new ReservationModel();
        List<Reservation> reservations = model.loadReservedReservations();

        // 출력 확인
        for (Reservation r : reservations) {
            System.out.printf("강의실: %d, 시작: %s, 종료: %s, 구분: %s%n",
                    r.getRoomNumber(), r.getStartTime(), r.getEndTime(), r.getType());
        }

        // 최소 하나 이상 예약이 있어야 테스트 통과 (상황에 맞게 조정)
        assertFalse(reservations.isEmpty(), "예약된 내역이 없습니다.");
    }
}