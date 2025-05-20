package view.student;

import com.google.gson.JsonObject;
import controller.student.ReservationService;
import model.student.Reservation;
import util.student.JsonDataHandler;
import view.login.LoginView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.Arrays;

// 학생 예약 UI 프레임 클래스
public class StudentReservationFrame extends JFrame {
    private ReservationService reservationService;
    private JComboBox<String> roomComboBox;
    private JComboBox<String> dayComboBox;
    private JComboBox<String> timeComboBox;
    private JTextField purposeField;
    private JTextArea reservationListArea;
    private Map<String, List<String>> roomSchedules;
    private JButton cancelButton;
    private List<Reservation> currentReservations;
    private JLabel roomTypeLabel;
    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private String studentId;
    private String studentName;
    private String studentRole;
    private List<JsonObject> roomList = new ArrayList<>();
    private JPanel mainPanel;
    private JPanel userInfoPanel;

    public StudentReservationFrame(String studentId, String studentName, String studentRole) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRole = studentRole;
        this.reservationService = new ReservationService();
        this.roomSchedules = new HashMap<>();
        this.currentReservations = new ArrayList<>();
        setTitle("강의실 예약 시스템 - 학생");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeComponents();
        loadRoomSchedules();
        updateReservationTable();
    }

    private void initializeComponents() {
        // 메인 패널 설정
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // 상단 패널 (사용자 정보 + 로그아웃 버튼)
        JPanel topPanel = new JPanel(new BorderLayout());
        userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // 사용자 정보 표시
        userInfoPanel.add(new JLabel("사용자 정보: "));
        userInfoPanel.add(new JLabel("이름: " + studentName));
        userInfoPanel.add(new JLabel("역할: " + studentRole));
        userInfoPanel.add(new JLabel("학번: " + studentId));
        
        topPanel.add(userInfoPanel, BorderLayout.WEST);

        // 로그아웃 버튼 추가
        JButton logoutButton = new JButton("로그아웃");
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "로그아웃 하시겠습니까?",
                "로그아웃",
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                dispose(); // 현재 창 닫기
                new LoginView(); // 로그인 화면으로 돌아가기
            }
        });
        topPanel.add(logoutButton, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 예약 패널 설정
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 강의실 선택
        inputPanel.add(new JLabel("강의실:"));
        roomComboBox = new JComboBox<>();
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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton reserveButton = new JButton("예약하기");
        JButton cancelButton = new JButton("예약취소");
        buttonPanel.add(reserveButton);
        buttonPanel.add(cancelButton);

        // 예약 내역 테이블
        String[] columnNames = {"예약 ID", "강의실", "날짜", "시작 시간", "종료 시간", "상태", "예약자 이름"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(reservationTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 300));

        // 이벤트 리스너
        roomComboBox.addActionListener(e -> {
            updateTimeSlots();
            updateReservationTable();
        });

        dayComboBox.addActionListener(e -> {
            updateTimeSlots();
            updateReservationTable();
        });

        reserveButton.addActionListener(e -> makeReservation());
        cancelButton.addActionListener(e -> cancelReservation());

        // 레이아웃 구성
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        
        // 버튼 패널과 테이블을 포함할 패널
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        loadRoomNumbers();
    }

    private void loadRoomNumbers() {
        try {
            System.out.println("강의실 정보 로딩 시작...");
            String filePath = "/Users/leeng/Desktop/2025_project/deu_class/Lab_room.json";
            System.out.println("파일 경로: " + filePath);
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            System.out.println("파일 내용 읽기 성공");
            JsonArray rootArray = new Gson().fromJson(content, JsonArray.class);
            System.out.println("강의실 개수: " + rootArray.size());
            roomList.clear();
            roomComboBox.removeAllItems();
            
            // 이미 로드된 강의실 번호를 추적하기 위한 Set
            java.util.Set<String> loadedRoomNumbers = new java.util.HashSet<>();
            
            for (JsonElement elem : rootArray) {
                JsonObject room = elem.getAsJsonObject();
                String roomNumber = room.get("roomNumber").getAsString();
                
                // 중복된 강의실 번호는 건너뛰기
                if (!loadedRoomNumbers.contains(roomNumber)) {
                    loadedRoomNumbers.add(roomNumber);
                    roomList.add(room);
                    roomComboBox.addItem(roomNumber);
                }
            }
            System.out.println("강의실 정보 로딩 완료");
        } catch (IOException e) {
            System.out.println("강의실 정보 로딩 실패: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "강의실 정보를 불러오는데 실패했습니다: " + e.getMessage());
        }
    }

    private void loadRoomSchedules() {
        try {
            System.out.println("강의실 일정 로딩 시작...");
            String filePath = "/Users/leeng/Desktop/2025_project/deu_class/Lab_room.json";
            System.out.println("파일 경로: " + filePath);
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            System.out.println("파일 내용 읽기 성공");
            JsonArray rootArray = new Gson().fromJson(content, JsonArray.class);
            System.out.println("강의실 개수: " + rootArray.size());
            
            roomSchedules.clear();
            for (JsonElement elem : rootArray) {
                JsonObject room = elem.getAsJsonObject();
                String roomNumber = room.get("roomNumber").getAsString();
                String day = room.get("day").getAsString();
                String state = room.get("state").getAsString();
                
                if (state.equals("O")) {
                    JsonArray timeSlots = room.getAsJsonArray("timeSlots");
                    List<String> slots = new ArrayList<>();
                    for (JsonElement slotElem : timeSlots) {
                        slots.add(slotElem.getAsString());
                    }
                    
                    if (!roomSchedules.containsKey(roomNumber)) {
                        roomSchedules.put(roomNumber, new ArrayList<>());
                    }
                    roomSchedules.get(roomNumber).addAll(slots);
                }
            }
            System.out.println("강의실 일정 로딩 완료");
        } catch (IOException e) {
            System.out.println("강의실 일정 로딩 실패: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "강의실 일정을 불러오는데 실패했습니다: " + e.getMessage());
        }
    }

    private void updateTimeSlots() {
        timeComboBox.removeAllItems();
        String selectedRoom = (String) roomComboBox.getSelectedItem();
        String selectedDay = (String) dayComboBox.getSelectedItem();
        
        if (selectedRoom != null && selectedDay != null) {
            for (JsonObject room : roomList) {
                if (room.get("roomNumber").getAsString().equals(selectedRoom) &&
                    room.get("day").getAsString().equals(selectedDay) &&
                    room.get("state").getAsString().equals("O")) {
                    
                    JsonArray timeSlots = room.getAsJsonArray("timeSlots");
                    for (JsonElement slotElem : timeSlots) {
                        timeComboBox.addItem(slotElem.getAsString());
                    }
                }
            }
        }
    }

    private void updateReservationTable() {
        tableModel.setRowCount(0);
        List<Reservation> reservations = reservationService.getReservationsByStudentId(studentId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Reservation reservation : reservations) {
            // 취소된 예약은 표시하지 않음
            if ("취소".equals(reservation.getState())) {
                continue;
            }
            
            Object[] rowData = {
                reservation.getId(),
                reservation.getRoomNumber(),
                reservation.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                reservation.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                reservation.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                reservation.getState(),
                studentName
            };
            tableModel.addRow(rowData);
        }
    }

    private void makeReservation() {
        String selectedRoom = (String) roomComboBox.getSelectedItem();
        String selectedDay = (String) dayComboBox.getSelectedItem();
        String selectedTime = (String) timeComboBox.getSelectedItem();
        String purpose = purposeField.getText().trim();

        if (selectedRoom == null || selectedDay == null || selectedTime == null || purpose.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 입력해주세요.");
            return;
        }

        // 예약 시간 계산
        String[] timeParts = selectedTime.split("~");
        if (timeParts.length != 2) {
            JOptionPane.showMessageDialog(this, "시간 형식이 올바르지 않습니다.");
            return;
        }
        
        String startTimeStr = timeParts[0].trim();
        String endTimeStr = timeParts[1].trim();

        // 현재 날짜 기준으로 예약 날짜 설정
        LocalDateTime now = LocalDateTime.now();
        int dayOfWeek = now.getDayOfWeek().getValue();
        int targetDay = getDayNumber(selectedDay);
        int daysToAdd = (targetDay - dayOfWeek + 7) % 7;
        LocalDateTime reservationDate = now.plusDays(daysToAdd);

        // 시작 시간과 종료 시간 설정
        LocalDateTime startTime = reservationDate
                .withHour(Integer.parseInt(startTimeStr.split(":")[0]))
                .withMinute(Integer.parseInt(startTimeStr.split(":")[1]));
        LocalDateTime endTime = reservationDate
                .withHour(Integer.parseInt(endTimeStr.split(":")[0]))
                .withMinute(Integer.parseInt(endTimeStr.split(":")[1]));

        Reservation reservation = new Reservation(
                UUID.randomUUID().toString(),
                selectedRoom,
                startTime,
                endTime,
                purpose,
                studentId
        );

        if (reservationService.makeReservation(reservation)) {
            JOptionPane.showMessageDialog(this, "예약이 완료되었습니다.");
            updateReservationTable();
            purposeField.setText("");  // 목적 필드 초기화
        } else {
            JOptionPane.showMessageDialog(this, "해당 시간에 이미 예약이 있습니다.");
        }
    }

    private void cancelReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "취소할 예약을 선택해주세요.");
            return;
        }

        String reservationId = (String) tableModel.getValueAt(selectedRow, 0);
        String state = (String) tableModel.getValueAt(selectedRow, 5);
        
        if (!"승인".equals(state)) {
            JOptionPane.showMessageDialog(this, "승인된 예약만 취소할 수 있습니다.");
            return;
        }

        System.out.println("취소할 예약 ID: " + reservationId);
        
        if (reservationId == null) {
            System.out.println("예약 ID가 null입니다.");
            JOptionPane.showMessageDialog(this, "예약 ID를 찾을 수 없습니다.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "선택한 예약을 취소하시겠습니까?",
            "예약 취소 확인",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean result = reservationService.cancelReservation(reservationId);
            System.out.println("예약 취소 결과: " + result);
            
            if (result) {
                JOptionPane.showMessageDialog(this, "예약이 취소되었습니다.");
                updateReservationTable();
            } else {
                JOptionPane.showMessageDialog(this, "예약 취소에 실패했습니다.");
            }
        }
    }

    private int getDayNumber(String day) {
        switch (day) {
            case "월요일": return 1;
            case "화요일": return 2;
            case "수요일": return 3;
            case "목요일": return 4;
            case "금요일": return 5;
            default: return 1;
        }
    }
} 