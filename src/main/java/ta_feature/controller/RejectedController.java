/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ta_feature.controller;
import ta_feature.model.Reservation;
import ta_feature.model.ReservationModel;
import ta_feature.view.rejectedFrame;

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

        DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "강의실", "시작 시간", "종료 시간", "구분" }, 0
        );

        for (Reservation r : rejectedList) {
            tableModel.addRow(new Object[] {
                r.getRoomNumber(),
                r.getStartTime(),
                r.getEndTime(),
                r.getType()
            });
        }

        view.setRejectedTableModel(tableModel);
    }
}