/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ta_feature.controller;
import ta_feature.model.ReservationModel;
import ta_feature.view.featureFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ReservationController {
    private final ReservationModel model;
    private final featureFrame view;

    public ReservationController(ReservationModel model, featureFrame view) {
        this.model = model;
        this.view = view;

        loadPendingReservations(); // 처음 실행 시 대기 목록 로드
    }

    // ✅ 대기 목록 불러오기
    public void loadPendingReservations() {
    DefaultTableModel model = this.model.loadPendingReservationsAll();
    view.setReservationTableModel(model);
}

    // ✅ 승인 처리 ("X" → "승인" + reservations_approved.json 저장)
    public void approveSelected(int rowIndex) {
    JTable table = view.getReservationTable();

    if (rowIndex == -1) {
        view.showSelectReservationMessage();  // 이 메시지 먼저 보여주고
        return;  // 더 이상 실행하지 않음
    }

    int roomNumber = Integer.parseInt(table.getValueAt(rowIndex, 0).toString());
    String day = table.getValueAt(rowIndex, 1).toString();
    String time = table.getValueAt(rowIndex, 2).toString();

    model.approveReservation(roomNumber, day, time, true);  // 예시: lab 기준
    loadPendingReservations();
    view.showApprovalMessage();  // 이건 선택이 된 경우에만 실행됨
}

    // ✅ 거절 처리 ("X" → "O" + reservations_rejected.json 저장)
    public void rejectSelected(int rowIndex) {
        JTable table = view.getReservationTable();

        int roomNumber = Integer.parseInt(table.getValueAt(rowIndex, 0).toString());
        String day = table.getValueAt(rowIndex, 1).toString();
        String time = table.getValueAt(rowIndex, 2).toString();

        model.rejectReservation(roomNumber, day, time, true); // 내부에서 JSON 저장까지 포함됨

        loadPendingReservations();
        view.showRejectionMessage();
    }
}