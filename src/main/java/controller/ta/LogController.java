/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.ta;

import view.ta.LogFrame;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class LogController {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private LogFrame view;
    private Socket socket;

    public LogController(LogFrame view, Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        this.view = view;
        this.socket = socket;
        this.out = out;
        this.in = in;
    }

    public void loadLog() {
        loadLog(view.getLogTextArea());
    }

    public void loadLog(JTextArea textArea) {
        try {
            out.writeObject("loadLog");
            out.flush();

            Object response = in.readObject();

            if (!(response instanceof List)) {
                textArea.setText("서버에서 올바른 로그 데이터를 받지 못했습니다.");
                return;
            }

            @SuppressWarnings("unchecked")
            List<Map<String, String>> logList = (List<Map<String, String>>) response;

            StringBuilder logText = new StringBuilder();

            for (Map<String, String> logEntry : logList) {
                String entry = String.format(
                        "%s | %s | %s | %s | %s\n",
                        logEntry.getOrDefault("timestamp", ""),
                        logEntry.getOrDefault("transition", ""),
                        logEntry.getOrDefault("targetUser", ""),
                        logEntry.getOrDefault("room", ""),
                        logEntry.getOrDefault("time", "")
                );
                logText.append(entry);
            }

            textArea.setText(logText.toString());

        } catch (Exception e) {
            textArea.setText("로그 불러오기 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}