/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.ta;
import view.ta.ManagerFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ManagerController {
    private ManagerFrame view;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ManagerController(ManagerFrame view, Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        this.view = view;
        this.socket = socket;
        this.out = out;
        this.in = in;
    }

    public void loadUsersToTable() {
        try {
            out.writeObject("loadUsers");
            out.flush();

            Object res = in.readObject();
            if (!(res instanceof List)) return;

            @SuppressWarnings("unchecked")
            List<Map<String, String>> users = (List<Map<String, String>>) res;

            DefaultTableModel model = (DefaultTableModel) view.getUserTable().getModel();
            model.setRowCount(0);

            for (Map<String, String> user : users) {
                model.addRow(new Object[]{
                        user.getOrDefault("id", ""),
                        user.getOrDefault("name", ""),
                        user.getOrDefault("department", ""),
                        user.getOrDefault("role", "")
                });
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
                out.writeObject("deleteUser");
                out.writeObject(userId);
                out.flush();

                Object result = in.readObject();
                if ("success".equals(result)) {
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