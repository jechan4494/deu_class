/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.ta;
import model.ta.Reservation;
import model.ta.ReservationModel;
import view.ta.rejectedFrame;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class RejectedController {
    private ReservationModel model;
    private rejectedFrame view;

    public RejectedController(ReservationModel model, rejectedFrame view) {
        this.model = model;
        this.view = view;
    }

    public void loadRejectedReservations() {
    List<Reservation> rejectedList = model.loadRejectedReservations();

    // ✅ 요일 컬럼 포함
    DefaultTableModel tableModel = new DefaultTableModel(
        new String[] { "강의실", "요일", "시작 시간", "종료 시간", "구분" }, 0
    );

    for (Reservation r : rejectedList) {
        tableModel.addRow(new Object[] {
            r.getRoomNumber(),
            r.getDay(),            // ✅ 요일 추가
            r.getStartTime(),
            r.getEndTime(),
            r.getType()
        });
    }
    System.out.println("📥 컨트롤러에서 받은 예약 수: " + rejectedList.size());
    view.setRejectedTableModel(tableModel);
}
}
