package view.components;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CalendarPanel extends JPanel {
    private JComboBox<String> roomComboBox;
    private JComboBox<LocalDate> dateComboBox;
    private JComboBox<LocalTime> startTimeComboBox;
    private JComboBox<LocalTime> endTimeComboBox;
    
    public CalendarPanel() {
        setLayout(new GridLayout(4, 2, 10, 10));
        
        // 강의실 선택
        add(new JLabel("강의실:"));
        roomComboBox = new JComboBox<>();
        // 강의실 데이터 로드
        add(roomComboBox);
        
        // 날짜 선택
        add(new JLabel("날짜:"));
        dateComboBox = new JComboBox<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 14; i++) { // 2주간 예약 가능
            dateComboBox.addItem(today.plusDays(i));
        }
        add(dateComboBox);
        
        // 시작 시간 선택 (30분 단위)
        add(new JLabel("시작 시간:"));
        startTimeComboBox = new JComboBox<>();
        for (int hour = 9; hour < 18; hour++) { // 09:00 ~ 18:00
            startTimeComboBox.addItem(LocalTime.of(hour, 0));
            startTimeComboBox.addItem(LocalTime.of(hour, 30));
        }
        add(startTimeComboBox);
        
        // 종료 시간 선택 (시작 시간 이후만 가능)
        add(new JLabel("종료 시간:"));
        endTimeComboBox = new JComboBox<>();
        updateEndTimeOptions();
        startTimeComboBox.addActionListener(e -> updateEndTimeOptions());
        add(endTimeComboBox);
    }
    
    private void updateEndTimeOptions() {
        LocalTime startTime = (LocalTime) startTimeComboBox.getSelectedItem();
        endTimeComboBox.removeAllItems();
        
        if (startTime != null) {
            LocalTime time = startTime.plusMinutes(30); // 최소 30분 예약
            while (time.isBefore(LocalTime.of(18, 30))) { // 18:30까지 예약 가능
                endTimeComboBox.addItem(time);
                time = time.plusMinutes(30);
            }
        }
    }
    
    // Getter 메서드들...
}