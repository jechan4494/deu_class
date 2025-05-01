/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ta_feature.controller;
import ta_feature.model.ReservationModel;
import ta_feature.view.featureFrame;
import javax.swing.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author rlarh
 */
public class ReservationController {
     private final ReservationModel model;
    private final featureFrame view;

    public ReservationController(ReservationModel model, featureFrame view) {
        this.model = model;
        this.view = view;

        // 버튼 이벤트 연결
        
        loadPendingReservations(); // 처음 실행 시 대기 목록 로드
    }
    public void approveReservation(int rowIndex, JTable table) {
    model.approveReservation(rowIndex, table);
}

public void rejectReservation(int rowIndex, JTable table) {
    model.rejectReservation(rowIndex, table);
}
   public void loadPendingReservations() {
    DefaultTableModel model = this.model.loadPendingReservations(); // ← 모델에서 데이터 가져옴
    view.setReservationTableModel(model); // ← 테이블에 적용
}

    // 🔽 승인 처리
   public void approveSelected(int rowIndex) {
    JTable table = view.getReservationTable();
    model.approveReservation(rowIndex, table);
    loadPendingReservations();  // ✅ 테이블을 새로 불러오기
    view.showApprovalMessage();
   }

    // 🔽 거절 처리
    public void rejectSelected(int row) {
        JTable table = view.getReservationTable();
        model.rejectReservation(row, table);
        loadPendingReservations();
        view.showRejectionMessage();
    }
}