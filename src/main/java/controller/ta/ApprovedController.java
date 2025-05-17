/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.ta;
import model.ta.Reservation;
import model.ta.ReservationModel;
import view.ta.ApprovedFrame;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ApprovedController {

    private ReservationModel model;
    private ApprovedFrame view;

    public ApprovedController(ReservationModel model, ApprovedFrame view) {
        this.model = model;
        this.view = view;
    }

    public void loadApprovedReservations() {
    List<Reservation> approvedList = model.loadApprovedReservations();

    DefaultTableModel tableModel = new DefaultTableModel(
    new String[] { "이름", "역할", "강의실 유형", "강의실 번호", "요일", "시간대", "상태" }, 0
);

    for (Reservation r : approvedList) {
    tableModel.addRow(new Object[] {
        r.getName(),
        r.getRole(),
        r.getType(),
        r.getRoomNumber(),
        r.getDay(),
        String.join(", ", r.getTimeSlots()),
        r.getState()
    });
    }

    System.out.println("✅ 컨트롤러에서 받은 예약 수: " + approvedList.size());
    view.setApprovedTableModel(tableModel);
}
}

