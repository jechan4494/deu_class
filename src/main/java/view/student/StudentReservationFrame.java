package view.student;

import model.ta.Reservation;
import service.student.ReservationService;
import model.room.RoomModel;
import util.JsonDataHandler;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StudentReservationFrame extends JFrame {
    private final ReservationService reservationService;
    private final String userName;
    private transient RoomModel roomModel;
    private transient JComboBox<String> roomTypeCombo;
    private transient JComboBox<Integer> roomNumberCombo;
    private transient JComboBox<String> dayCombo;
    private transient JList<String> timeSlotsList;
    
    public StudentReservationFrame(String userName) {
        this.userName = userName;
        this.reservationService = new ReservationService();
        initComponents();
        updateRoomModel();
    }
    
    public StudentReservationFrame() {
        this("이나겸"); // 기본 생성자는 테스트용으로만 사용
    }
    
    private void initComponents() {
        setTitle("학생 예약 시스템");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // 메인 패널
        JPanel mainPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 컴포넌트 초기화
        roomTypeCombo = new JComboBox<>(new String[]{"일반실", "실습실"});
        roomTypeCombo.addActionListener(e -> {
            updateRoomModel();
            updateTimeSlots();
        });
        
        roomNumberCombo = new JComboBox<>(new Integer[]{913, 914, 915, 916, 917, 918});
        roomNumberCombo.addActionListener(e -> updateTimeSlots());
        
        dayCombo = new JComboBox<>(new String[]{"월요일", "화요일", "수요일", "목요일", "금요일"});
        dayCombo.addActionListener(e -> updateTimeSlots());
        
        timeSlotsList = new JList<>();
        timeSlotsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        timeSlotsList.setVisibleRowCount(5);  // 한 번에 보이는 행 수 설정
        timeSlotsList.setFixedCellHeight(25); // 셀 높이 설정
        timeSlotsList.setFixedCellWidth(100); // 셀 너비 설정
        
        // 시간대 선택 설명 레이블 추가
        JLabel timeSlotLabel = new JLabel("<html>시간대 선택<br>(최대 3개 선택 가능)</html>");
        JPanel timeSlotPanel = new JPanel(new BorderLayout());
        timeSlotPanel.add(timeSlotLabel, BorderLayout.NORTH);
        timeSlotPanel.add(new JScrollPane(timeSlotsList), BorderLayout.CENTER);
        
        // 컴포넌트 추가
        mainPanel.add(new JLabel("강의실 유형:"));
        mainPanel.add(roomTypeCombo);
        mainPanel.add(new JLabel("강의실 번호:"));
        mainPanel.add(roomNumberCombo);
        mainPanel.add(new JLabel("요일:"));
        mainPanel.add(dayCombo);
        mainPanel.add(new JLabel("시간대:"));
        mainPanel.add(timeSlotPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        JButton submitButton = new JButton("예약하기");
        submitButton.addActionListener(e -> submitReservation());
        add(submitButton, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        
        // 초기 시간대 목록 업데이트
        updateTimeSlots();
    }
    
    private void updateRoomModel() {
        String selectedType = roomTypeCombo.getSelectedItem().toString();
        String jsonPath = selectedType.equals("실습실") 
            ? "deu_class/src/main/resources/Lab_room.json" 
            : "deu_class/src/main/resources/normal_room.json";
        roomModel = new RoomModel(jsonPath);
    }
    
    private void updateTimeSlots() {
        Integer roomNumber = (Integer) roomNumberCombo.getSelectedItem();
        String day = dayCombo.getSelectedItem().toString();
        
        if (roomModel != null && roomNumber != null && day != null) {
            List<String> availableSlots = roomModel.getTimeSlots(roomNumber, day);
            timeSlotsList.setListData(availableSlots.toArray(new String[0]));
        }
    }
    
    private void submitReservation() {
        List<String> selectedTimes = timeSlotsList.getSelectedValuesList();
        if (selectedTimes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "시간을 선택해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedTimes.size() > 3) {
            JOptionPane.showMessageDialog(this, "최대 3개의 시간대만 선택할 수 있습니다.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 선택한 시간대가 예약 가능한지 확인
        Integer roomNumber = (Integer) roomNumberCombo.getSelectedItem();
        String day = dayCombo.getSelectedItem().toString();
        
        for (String timeSlot : selectedTimes) {
            if (!roomModel.isReservable(roomNumber, day, timeSlot)) {
                JOptionPane.showMessageDialog(this, 
                    "선택한 시간대 중 이미 예약된 시간이 있습니다.\n다른 시간을 선택해주세요.", 
                    "예약 불가", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        Reservation reservation = new Reservation(
            userName, // 로그인한 사용자의 이름 사용
            "STUDENT",
            roomTypeCombo.getSelectedItem().toString(),
            roomNumber,
            day,
            new ArrayList<>(selectedTimes),
            "대기"
        );
        
        reservationService.addReservation(reservation);
        
        // 예약된 시간 표시 업데이트
        String jsonPath = roomTypeCombo.getSelectedItem().toString().equals("실습실") 
            ? "deu_class/src/main/resources/Lab_room.json" 
            : "deu_class/src/main/resources/normal_room.json";
            
        for (String timeSlot : selectedTimes) {
            roomModel.markReserved(roomNumber, day, timeSlot, jsonPath);
        }
        
        JOptionPane.showMessageDialog(this, "예약이 신청되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentReservationFrame().setVisible(true);
        });
    }
} 