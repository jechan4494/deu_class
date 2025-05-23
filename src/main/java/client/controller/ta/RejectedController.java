/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.controller.ta;
import org.json.*;
import client.view.ta.rejectedFrame;

import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class RejectedController {
    private rejectedFrame view;
    private PrintWriter socketOut;
    private BufferedReader socketIn;

    public RejectedController(rejectedFrame view, PrintWriter socketOut, BufferedReader socketIn) {
        this.view = view;
        this.socketOut = socketOut;
        this.socketIn = socketIn;
    }

    public void loadRejectedReservations() {
        try {
            // 서버에 거절된 예약 요청
            JSONObject request = new JSONObject();
            request.put("type", "loadRejected");
            socketOut.println(request.toString());

            // 서버 응답 받기
            String response = socketIn.readLine();
            JSONArray arr = new JSONArray(response);

            // 테이블 생성
            DefaultTableModel tableModel = new DefaultTableModel(
                new String[]{"이름", "역할", "강의실 유형", "강의실 번호", "요일", "시간대", "상태"}, 0
            );

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                JSONArray timeSlots = obj.getJSONArray("timeSlots");
                String firstTimeSlot = timeSlots.length() > 0 ? timeSlots.getString(0) : "";

                tableModel.addRow(new Object[] {
                    obj.getString("name"),
                    obj.getString("role"),
                    obj.getString("roomType"),
                    obj.getInt("roomNumber"),
                    obj.getString("day"),
                    firstTimeSlot,
                    obj.getString("state")
                });
            }

            view.setRejectedTableModel(tableModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

