/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.ta;
import model.ta.Reservation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class LogController {

    // ✅ GitHub 공유 폴더에 있는 로그 파일 경로로 변경
    private static final String LOG_FILE = "deu_class/src/main/resources/ta_log.json";
    private final Gson gson;

    public LogController() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    // 로그 저장 메서드
    public void saveTaLog(String transition, Reservation reservation) {
        JsonObject log = new JsonObject();
        log.addProperty("transition", transition);
        log.addProperty("targetUser", reservation.getName());
        log.addProperty("room", reservation.getType() + " " + reservation.getRoomNumber());
        log.addProperty("time", reservation.getDay() + " " + String.join(", ", reservation.getTimeSlots()));
        log.addProperty("timestamp", LocalDateTime.now().toString());

        try {
            File file = new File(LOG_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            
            List<JsonObject> logs = new ArrayList<>();
            if (file.length() > 0) {
                try (Reader reader = new FileReader(file)) {
                    JsonObject[] existingLogs = gson.fromJson(reader, JsonObject[].class);
                    if (existingLogs != null) {
                        logs.addAll(Arrays.asList(existingLogs));
                    }
                }
            }
            
            logs.add(log);
            
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(logs, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 로그 불러와서 JTextArea에 출력
    public void loadTaLog(JTextArea textArea) {
        File file = new File(LOG_FILE);
        StringBuilder logText = new StringBuilder();

        if (file.exists() && file.length() > 0) {
            try (Reader reader = new FileReader(file)) {
                JsonObject[] logs = gson.fromJson(reader, JsonObject[].class);
                if (logs != null) {
                    for (JsonObject log : logs) {
                        logText.append(String.format("[%s] %s - %s (%s) %s\n",
                            log.get("timestamp").getAsString(),
                            log.get("transition").getAsString(),
                            log.get("targetUser").getAsString(),
                            log.get("room").getAsString(),
                            log.get("time").getAsString()
                        ));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                logText.append("로그를 불러오는 중 오류가 발생했습니다.");
            }
        } else {
            logText.append("로그가 없습니다.");
        }

        textArea.setText(logText.toString());
    }
}
