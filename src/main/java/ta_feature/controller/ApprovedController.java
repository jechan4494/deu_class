/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ta_feature.controller;
import ta_feature.model.ReservationModel;
import ta_feature.view.ApprovedFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ApprovedController {
    private final ReservationModel model;
    private final ApprovedFrame view;

    public ApprovedController(ReservationModel model, ApprovedFrame view) {
        this.model = model;
        this.view = view;
    }

    // 승인된 예약 내역을 테이블에 로드
    public void loadApprovedReservations() {
        DefaultTableModel tableModel = model.loadApprovedReservations();
        view.setApprovedTableModel(tableModel);
    }

    // 승인된 예약을 거절로 변경
    public void rejectFromApproved(int rowIndex) {
        JTable table = view.getApprovedTable();
        String[] rowData = new String[6];
        for (int i = 0; i < 6; i++) {
            rowData[i] = table.getValueAt(rowIndex, i).toString();
        }

        model.moveToRejectedFromApproved(rowData); // 모델에 위임
        model.appendLog("승인", "거절", rowData[0], rowData[1], rowData[2],
                        rowData[3], rowData[4]);

        loadApprovedReservations();      // 테이블 새로고침
        view.showRejectMessage();        // 사용자에게 메시지 출력
    }
}
