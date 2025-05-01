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

    // ✅ 승인된 예약 내역을 테이블에 로드
    public void loadApprovedReservations(boolean isLabRoom) {
        DefaultTableModel tableModel = model.loadApprovedReservations(isLabRoom);
        view.setApprovedTableModel(tableModel);
    }

    // ✅ 승인된 예약을 거절로 변경
    public void rejectFromApproved(int rowIndex, boolean isLabRoom) {
        JTable table = view.getApprovedTable();

        // 테이블에서 값 추출
        int roomNumber = Integer.parseInt(table.getValueAt(rowIndex, 0).toString());
        String day = table.getValueAt(rowIndex, 1).toString();
        String time = table.getValueAt(rowIndex, 2).toString();

        // 1. 예약 상태를 "O"로 되돌림 (room JSON 갱신)
        model.rejectReservation(roomNumber, day, time, isLabRoom);

        // 2. 승인 JSON에서 제거 + 거절 JSON에 추가
        model.moveToRejectedFromApproved(roomNumber, day, time);

        // 3. 테이블 새로고침 및 사용자 알림
        loadApprovedReservations(isLabRoom);
        view.showRejectMessage();
    }
}
