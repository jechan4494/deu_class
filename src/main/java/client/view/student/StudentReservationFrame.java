package client.view.student;

import server.controller.student.ReservationService;
import server.model.student.Reservation;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// 학생 예약 UI 프레임 클래스
public class StudentReservationFrame extends JFrame {
    private ReservationService reservationService;
    private JComboBox<String> roomComboBox;
    private JComboBox<String> dayComboBox;
    private JComboBox<String> timeComboBox;
    private JTextField purposeField;
    private JTextArea reservationListArea;
    private Map<String, JsonObject> roomSchedules;

    public StudentReservationFrame(String studentId) {
        this.reservationService = new ReservationService();
        this.roomSchedules = new HashMap<>();
        setTitle("강의실 예약 시스템 - 학생");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeComponents();
        displayReservations(studentId);

        // 요일 선택 시 시간대 업데이트
        dayComboBox.addActionListener(e -> updateTimeSlots());
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        // 강의실 선택
        inputPanel.add(new JLabel("강의실:"));
        roomComboBox = new JComboBox<>(getRoomNumbers());
        inputPanel.add(roomComboBox);

        // 요일 선택
        inputPanel.add(new JLabel("요일:"));
        dayComboBox = new JComboBox<>(new String[]{"월요일", "화요일", "수요일", "목요일", "금요일"});
        inputPanel.add(dayComboBox);

        // 시간 선택
        inputPanel.add(new JLabel("시간:"));
        timeComboBox = new JComboBox<>();
        inputPanel.add(timeComboBox);

        // 목적
        inputPanel.add(new JLabel("목적:"));
        purposeField = new JTextField();
        inputPanel.add(purposeField);

        // 예약 버튼
        JButton reserveButton = new JButton("예약하기");
        reserveButton.addActionListener(e -> makeReservation());
        inputPanel.add(reserveButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // 예약 목록 표시 영역
        reservationListArea = new JTextArea();
        reservationListArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reservationListArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        
        // 초기 시간 슬롯 업데이트
        updateTimeSlots();
    }

    private void updateTimeSlots() {
        String selectedRoom = (String) roomComboBox.getSelectedItem();
        String selectedDay = (String) dayComboBox.getSelectedItem();
        
        if (selectedRoom != null && selectedDay != null) {
            JsonObject roomSchedule = roomSchedules.get(selectedRoom);
            if (roomSchedule != null) {
                JsonObject schedule = roomSchedule.getAsJsonObject("schedule");
                JsonArray timeSlots = schedule.getAsJsonArray(selectedDay);
                
                timeComboBox.removeAllItems();
                for (JsonElement slot : timeSlots) {
                    JsonObject timeSlot = slot.getAsJsonObject();
                    if (timeSlot.get("state").getAsString().equals("O")) {
                        timeComboBox.addItem(timeSlot.get("time").getAsString());
                    }
                }
            }
        }
    }

    private String[] getRoomNumbers() {
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get("src/main/java/Lab_room.json")));
            JsonArray roomsArray = JsonParser.parseString(jsonContent).getAsJsonArray()
                .get(0).getAsJsonObject().getAsJsonArray("rooms");
            
            String[] roomNumbers = new String[roomsArray.size()];
            for (int i = 0; i < roomsArray.size(); i++) {
                JsonObject room = roomsArray.get(i).getAsJsonObject();
                String roomNumber = room.get("roomNumber").getAsString();
                roomNumbers[i] = roomNumber;
                roomSchedules.put(roomNumber, room);
            }
            return roomNumbers;
        } catch (IOException e) {
            e.printStackTrace();
            return new String[]{"911", "915"};
        }
    }

    private void makeReservation() {
        try {
            String roomNumber = (String) roomComboBox.getSelectedItem();
            String selectedTime = (String) timeComboBox.getSelectedItem();
            String purpose = purposeField.getText();

            if (roomNumber == null || selectedTime == null || purpose.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 필드를 입력해주세요.");
                return;
            }

            // 시간 문자열 파싱 (예: "09:00-09:50")
            String[] timeRange = selectedTime.split("-");
            String startTimeStr = timeRange[0];
            String endTimeStr = timeRange[1];

            // 현재 날짜와 선택된 시간을 조합
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startTime = now.withHour(Integer.parseInt(startTimeStr.split(":")[0]))
                                       .withMinute(Integer.parseInt(startTimeStr.split(":")[1]));
            LocalDateTime endTime = now.withHour(Integer.parseInt(endTimeStr.split(":")[0]))
                                     .withMinute(Integer.parseInt(endTimeStr.split(":")[1]));

            Reservation reservation = new Reservation(
                    String.valueOf(System.currentTimeMillis()),
                    roomNumber,
                    startTime,
                    endTime,
                    purpose,
                    "student123"
            );

            if (reservationService.makeReservation(reservation)) {
                JOptionPane.showMessageDialog(this, "예약이 완료되었습니다.");
                displayReservations("student123");
            } else {
                JOptionPane.showMessageDialog(this, "해당 시간에 이미 예약이 있습니다.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "예약 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void displayReservations(String studentId) {
        List<Reservation> reservations = reservationService.getReservationsByStudentId(studentId);
        StringBuilder sb = new StringBuilder();
        for (Reservation r : reservations) {
            sb.append(String.format("강의실: %s, 시작: %s, 종료: %s, 목적: %s\n",
                    r.getRoomNumber(),
                    r.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    r.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    r.getPurpose()));
        }
        reservationListArea.setText(sb.toString());
    }
} 