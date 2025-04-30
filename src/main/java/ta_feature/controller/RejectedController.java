/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ta_feature.controller;
import ta_feature.model.ReservationModel;
import ta_feature.view.RejectedFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RejectedController {
    private final ReservationModel model;
    private final RejectedFrame view;

    public RejectedController(ReservationModel model, RejectedFrame view) {
        this.model = model;
        this.view = view;
    }

    // 거절된 예약을 테이블에 로드
    public void loadRejectedReservations() {
        DefaultTableModel tableModel = model.loadRejectedReservations();
        view.setRejectedTableModel(tableModel);
    }

    // 거절 → 승인 전환
    public void approveFromRejected(int rowIndex) {
        JTable table = view.getRejectedTable();
        String[] rowData = new String[6];
        for (int i = 0; i < 6; i++) {
            rowData[i] = table.getValueAt(rowIndex, i).toString();
        }

        model.moveToApprovedFromRejected(rowData);
        model.appendLog("거절", "승인", rowData[0], rowData[1], rowData[2],
                        rowData[3], rowData[4]);

        loadRejectedReservations(); // 테이블 새로고침
        view.showApproveMessage();  // 알림
    }
}
/**
 *
 * @author rlarh
 */
