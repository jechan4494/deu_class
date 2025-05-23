package client.controller.ta;
import org.json.*;
import client.view.ta.approvedFrame;

import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ApprovedController {

    private approvedFrame view;
    private PrintWriter socketOut;
    private BufferedReader socketIn;

    public ApprovedController(approvedFrame view, PrintWriter socketOut, BufferedReader socketIn) {
        this.view = view;
        this.socketOut = socketOut;
        this.socketIn = socketIn;
    }

    public void loadApprovedReservations() {
        try {
            // 서버에 승인된 예약 목록 요청
            JSONObject request = new JSONObject();
            request.put("type", "loadApproved");
            socketOut.println(request.toString());

            // 서버 응답 받기
            String response = socketIn.readLine();
            JSONArray arr = new JSONArray(response);

            // 테이블에 표시
            DefaultTableModel tableModel = new DefaultTableModel(
                new String[] {"이름", "역할", "강의실 유형", "강의실 번호", "요일", "시간대", "상태"}, 0
            );

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                tableModel.addRow(new Object[] {
                    obj.getString("name"),
                    obj.getString("role"),
                    obj.getString("roomType"),
                    obj.getInt("roomNumber"),
                    obj.getString("day"),
                    String.join(", ", toList(obj.getJSONArray("timeSlots"))),
                    obj.getString("state")
                });
            }

            view.setApprovedTableModel(tableModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> toList(JSONArray arr) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            list.add(arr.getString(i));
        }
        return list;
    }
}

