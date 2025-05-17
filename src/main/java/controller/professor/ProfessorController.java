package controller.professor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.ta.Reservation;
import model.user.User;
import view.login.LoginView;
import view.professor.ProfessorView;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProfessorController implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final String LAB_ROOM_PATH = "deu_class/src/main/resources/Lab_room.json";
    private static final String NORMAL_ROOM_PATH = "deu_class/src/main/resources/normal_room.json";
    private static final String RESERVATIONS_PATH = "deu_class/src/main/resources/reservations.json";
    private static final Type RESERVATION_LIST_TYPE = new TypeToken<List<Reservation>>(){}.getType();

    private final transient ProfessorView professorView;
    private final transient User user;
    private final transient Gson gson;

    public ProfessorController(ProfessorView professorView, User user) {
        this.professorView = professorView;
        this.user = user;
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        professorView.setReservationHandler(this::reserveRoom);
        initListeners();
    }

    public void reserveRoom(Integer room, String day, List<String> periods, String roomType) {
        String jsonPath = roomType.equals("실습실") ? LAB_ROOM_PATH : NORMAL_ROOM_PATH;

        // 하나라도 예약 불가인 시간대가 있으면 예약 중단
        for (String timeSlot : periods) {
            if (!professorView.getRoomModel().isReservable(room, day, timeSlot)) {
                showMessage("이미 예약된 시간대를 포함하고 있습니다.\n예약할 수 없습니다.",
                    "예약 불가", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        // Reservation 객체 생성
        Reservation reservation = new Reservation(
            user.getName(),
            user.getRole(),
            roomType,
            room,
            day,
            periods,
            "대기"
        );
        
        professorView.getRoomModel().saveReservation(reservation);

        // 예약이 완료된 모든 시간대를 "X"로 변경하고 파일에 바로 저장
        for (String timeSlot : periods) {
            professorView.getRoomModel().markReserved(room, day, timeSlot, jsonPath);
        }

        saveReservationEntry(reservation);
    }

    private void showMessage(String message, String title, int messageType) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(professorView, message, title, messageType)
        );
    }

    private void initListeners() {
        ActionListener startReservationListener = e -> {
            String[] options = {"실습실", "일반실"};
            int choice = JOptionPane.showOptionDialog(
                    professorView,
                    "예약할 강의실 유형을 선택하세요.",
                    "강의실 유형 선택",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice != JOptionPane.CLOSED_OPTION) {
                String jsonPath = (choice == 0) ? LAB_ROOM_PATH : NORMAL_ROOM_PATH;
                String roomType = options[choice];
                startReservation(jsonPath, roomType);
            }
        };

        ActionListener cancelReservationListener = e -> 
            showMessage("예약 취소 기능은 아직 구현되지 않았습니다.", 
                "기능 미구현", JOptionPane.INFORMATION_MESSAGE);

        ActionListener logoutListener = e -> {
            int confirm = JOptionPane.showConfirmDialog(professorView, 
                "로그아웃하시겠습니까?", 
                "로그아웃", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                professorView.dispose();
                SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
            }
        };

        professorView.getBtnStartReservation().addActionListener(startReservationListener);
        professorView.getBtnCancelReservation().addActionListener(cancelReservationListener);
        professorView.getBtnLogout().addActionListener(logoutListener);
    }

    public List<Reservation> loadActiveReservations(String filePath) {
        List<Reservation> result = new ArrayList<>();
        try (FileReader reader = new FileReader(filePath)) {
            List<Reservation> all = gson.fromJson(reader, RESERVATION_LIST_TYPE);
            if (all == null) {
                try (FileReader singleReader = new FileReader(filePath)) {
                    Reservation single = gson.fromJson(singleReader, Reservation.class);
                    if (single != null) {
                        result.add(single);
                    }
                }
            } else {
                result.addAll(all);
            }
        } catch (IOException e) {
            showMessage("예약 정보를 불러오는 중 오류가 발생했습니다: " + e.getMessage(),
                "오류", JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    public void startReservation(String jsonPath, String roomType) {
        professorView.showReservationUI(jsonPath, roomType);
    }

    public void saveReservationEntry(Reservation reservation) {
        try {
            File file = new File(RESERVATIONS_PATH);
            List<Reservation> entries = new ArrayList<>();
            
            if (file.exists() && file.length() > 0) {
                try (FileReader reader = new FileReader(file)) {
                    List<Reservation> existingEntries = gson.fromJson(reader, RESERVATION_LIST_TYPE);
                    if (existingEntries != null) {
                        entries.addAll(existingEntries);
                    }
                }
            }
            
            entries.add(reservation);
            
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(entries, writer);
            }
            
            showMessage("예약이 완료되었습니다.", "예약 성공", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            showMessage("예약 저장 중 오류가 발생했습니다: " + e.getMessage(),
                "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}