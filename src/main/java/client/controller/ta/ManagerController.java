/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.controller.ta;
import client.view.ta.ManagerFrame;
import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;
import org.json.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ManagerController {
    private ManagerFrame view;
    private PrintWriter socketOut;
    private BufferedReader socketIn;
    private Socket socket;

    public ManagerController(ManagerFrame view, PrintWriter out, BufferedReader in) {
        this.view = view;
        this.socket = socket;
        this.socketOut = out;
        this.socketIn = in;
    }

    public void loadUsersToTable() {
        try {
            JSONObject req = new JSONObject();
            req.put("type", "loadUsers");
            socketOut.println(req.toString());

            String res = socketIn.readLine();
            JSONArray arr = new JSONArray(res);

            DefaultTableModel model = (DefaultTableModel) view.getUserTable().getModel();
            model.setRowCount(0);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String[] row = {
                    obj.optString("id"),
                    obj.optString("name"),
                    obj.optString("department"),
                    obj.optString("role")
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSelectedUser(int rowIndex) {
        JTable table = view.getUserTable();
        if (rowIndex == -1) {
            JOptionPane.showMessageDialog(view, "삭제할 계정을 선택하세요.");
            return;
        }

        String userId = table.getValueAt(rowIndex, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "정말로 이 계정을 삭제하시겠습니까?",
                "계정 삭제 확인",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                JSONObject req = new JSONObject();
                req.put("type", "deleteUser");
                req.put("userId", userId);
                socketOut.println(req.toString());

                String response = socketIn.readLine();
                JSONObject res = new JSONObject(response);

                if (res.getString("status").equals("success")) {
                    JOptionPane.showMessageDialog(view, "계정이 삭제되었습니다.");
                    loadUsersToTable();
                } else {
                    JOptionPane.showMessageDialog(view, "계정 삭제에 실패했습니다.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public Socket getSocket() {
        return socket;
    }
}