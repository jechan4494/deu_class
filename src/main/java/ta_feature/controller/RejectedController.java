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
    public void loadRejectedReservations(boolean isLabRoom) {
        DefaultTableModel tableModel = model.loadRejectedReservations(isLabRoom);
        view.setRejectedTableModel(tableModel);
    }

    // 거절 → 승인 전환
    public void approveFromRejected(int rowIndex, boolean isLabRoom) {
        JTable table = view.getRejectedTable();

        int roomNumber = Integer.parseInt(table.getValueAt(rowIndex, 0).toString());
        String day = table.getValueAt(rowIndex, 1).toString();
        String time = table.getValueAt(rowIndex, 2).toString();

        model.approveReservation(roomNumber, day, time, isLabRoom);  // 모델이 파일 저장까지 처리함

        loadRejectedReservations(isLabRoom);  // 테이블 갱신
        view.showApproveMessage();  // 사용자에게 알림
    }
}

