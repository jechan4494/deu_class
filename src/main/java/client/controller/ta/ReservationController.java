/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.controller.ta;
import org.json.JSONArray;
import org.json.JSONObject;
import client.view.ta.featureFrame;
import java.io.BufferedReader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ReservationController {
    private Socket socket;
private PrintWriter socketOut;
private BufferedReader socketIn;
    private final featureFrame view;

public ReservationController(featureFrame view, Socket socket, PrintWriter socketOut, BufferedReader socketIn) {
    this.view = view;
    this.socket = socket;
    this.socketOut = socketOut;
    this.socketIn = socketIn;
}
    public void loadReservedDataToTable() {
    try {
        JSONObject request = new JSONObject();
        request.put("type", "loadReserved");
        socketOut.println(request.toString());
        socketOut.flush();  // 🔒 반드시 flush!

        System.out.println("서버에 loadReserved 요청 전송");

        String response = socketIn.readLine();
        System.out.println("서버 응답 수신: " + response);

        if (response == null) {
            JOptionPane.showMessageDialog(view, "서버로부터 응답을 받지 못했습니다.");
            return;
        }

        JSONArray arr = new JSONArray(response);
        System.out.println("JSON 파싱 성공, 항목 수: " + arr.length());

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"이름", "역할", "강의실 유형", "강의실 번호", "요일", "시간대", "상태"}, 0
        );

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            model.addRow(new Object[]{
                obj.getString("name"),
                obj.getString("role"),
                obj.getString("roomType"),
                obj.getInt("roomNumber"),
                obj.getString("day"),
                String.join(", ", toList(obj.getJSONArray("timeSlots"))),
                obj.getString("state")
            });
        }

        view.setReservationTableModel(model);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(view, "예약 내역 불러오기 중 오류 발생");
    }
}

private List<String> toList(JSONArray arr) {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < arr.length(); i++) {
        list.add(arr.getString(i));
    }
    return list;
}
    public void approveSelectedReservation(int selectedRow) {
    JTable table = view.getReservationTable();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(view, "예약을 선택하세요.");
        return;
    }

    DefaultTableModel model = (DefaultTableModel) table.getModel();

    String name = (String) model.getValueAt(selectedRow, 0);
    String role = (String) model.getValueAt(selectedRow, 1);
    String roomType = (String) model.getValueAt(selectedRow, 2);
    int roomNumber = (int) model.getValueAt(selectedRow, 3);
    String day = (String) model.getValueAt(selectedRow, 4);
    String timeSlot = (String) model.getValueAt(selectedRow, 5);

    List<String> timeSlots = new ArrayList<>();
    timeSlots.add(timeSlot);

    JSONObject request = new JSONObject();
    request.put("type", "approve");
    request.put("name", name);
    request.put("role", role);
    request.put("roomType", roomType);
    request.put("roomNumber", roomNumber);
    request.put("day", day);
    request.put("timeSlots", new JSONArray(timeSlots));

    try {
        socketOut.println(request.toString());
        socketOut.flush();

        // ✅ 서버 응답 받기
        String response = socketIn.readLine();
        JSONObject result = new JSONObject(response);
        if ("success".equals(result.getString("result"))) {
            JOptionPane.showMessageDialog(view, "예약이 승인되었습니다.");
        } else {
            JOptionPane.showMessageDialog(view, "승인 처리에 실패했습니다.");
        }

        // ✅ 테이블 갱신
        Timer timer = new Timer(200, e -> loadReservedDataToTable());
        timer.setRepeats(false);
        timer.start();
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(view, "서버와 통신 중 오류 발생");
    }
    }

    public void rejectSelectedReservation(int selectedRow) {
    JTable table = view.getReservationTable();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(view, "예약을 선택하세요.");
        return;
    }

    DefaultTableModel model = (DefaultTableModel) table.getModel();

    String name = (String) model.getValueAt(selectedRow, 0);
    String role = (String) model.getValueAt(selectedRow, 1);
    String roomType = (String) model.getValueAt(selectedRow, 2);
    int roomNumber = (int) model.getValueAt(selectedRow, 3);
    String day = (String) model.getValueAt(selectedRow, 4);
    String timeSlot = (String) model.getValueAt(selectedRow, 5);

    List<String> timeSlots = new ArrayList<>();
    timeSlots.add(timeSlot);

    JSONObject request = new JSONObject();
    request.put("type", "reject");
    request.put("name", name);
    request.put("role", role);
    request.put("roomType", roomType);
    request.put("roomNumber", roomNumber);
    request.put("day", day);
    request.put("timeSlots", new JSONArray(timeSlots));

    try {
        socketOut.println(request.toString());
        socketOut.flush();

        // ✅ 서버 응답 받기
        String response = socketIn.readLine();
        JSONObject result = new JSONObject(response);
        if ("success".equals(result.getString("result"))) {
            JOptionPane.showMessageDialog(view, "예약이 거절되었습니다.");
        } else {
            JOptionPane.showMessageDialog(view, "거절 처리에 실패했습니다.");
        }

        // ✅ 테이블 갱신
        Timer timer = new Timer(200, e -> loadReservedDataToTable());
        timer.setRepeats(false);
        timer.start();
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(view, "서버와 통신 중 오류 발생");
    }
    }
    public PrintWriter getSocketOut() { return socketOut; }
    public BufferedReader getSocketIn() { return socketIn; }

    public Socket getSocket() {
         return socket;
    }
}