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

    // 승인 내역 불러오기
    public void loadApprovedReservations() {
        DefaultTableModel tableModel = model.loadApprovedReservations();
        view.setApprovedTableModel(tableModel);
    }

    // 승인된 예약 → 거절로 이동
public void rejectFromApproved(int rowIndex) {
    JTable table = view.getApprovedTable();
    String[] rowData = new String[7];
    for (int i = 0; i < 6; i++) {
        rowData[i] = table.getValueAt(rowIndex, i).toString();
    }
    rowData[6] = "승인"; // 기존 상태

    model.moveToRejectedFromApproved(rowData);
    model.appendLog("승인", "거절", rowData[0], rowData[1], rowData[2], rowData[3], rowData[4], rowData[5]);

    loadApprovedReservations(); // 테이블 새로고침
}
}
