/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.controller.ta;

import client.view.ta.LogFrame;
import org.json.*;
import javax.swing.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.PrintWriter;

public class LogController {
    private BufferedReader socketIn;
    private PrintWriter socketOut;
    private LogFrame view;
    
    public LogController(LogFrame view, PrintWriter out, BufferedReader in) {
    this.view = view;
    this.socketOut = out;
    this.socketIn = in;
}
    public void loadLog(){
        loadLog(view.getLogTextArea());
    }

    public void loadLog(JTextArea textArea) {
        try {
            JSONObject request = new JSONObject();
            request.put("type", "loadLog");
            socketOut.println(request.toString());

            String response = socketIn.readLine();
            JSONArray logArray = new JSONArray(response);

            StringBuilder logText = new StringBuilder();

            for (int i = 0; i < logArray.length(); i++) {
                JSONObject obj = logArray.getJSONObject(i);

                String entry = String.format(
                    "%s | %s | %s | %s | %s\n",
                    obj.optString("timestamp"),
                    obj.optString("transition"),
                    obj.optString("targetUser"),
                    obj.optString("room"),
                    obj.optString("time")
                );

                logText.append(entry);
            }

            textArea.setText(logText.toString());

        } catch (Exception e) {
            textArea.setText("로그 불러오기 실패: " + e.getMessage());
        }
    }
}