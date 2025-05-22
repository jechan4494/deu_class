package controller.student;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.room.RoomModel;
import model.room.RoomReservation;
import model.user.User;
import view.login.LoginView;
import view.student.StudentView;

import javax.swing.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class StudentReserveController {
    private final StudentView studentView;
    private final User user;

    public StudentReserveController(StudentView studentView, User user) {
        this.studentView = studentView;
        this.user = user;

        studentView.setReservationHandler(this::reserveRoom);
        initListeners();
    }

    public void reserveRoom(Integer room, String day, List<String> periods, String roomType) {
        String jsonPath = roomType.equals("실습실") ? "src/Lab_room.json" : "src/normal_room.json";
        RoomModel roomModel = studentView.roomModel; // 뷰 안에 인스턴스 존재한다고 가정

        // 하나라도 예약 불가인 시간대가 있으면 예약 중단
        for (String timeSlot : periods) {
            if (!roomModel.isReservable(room, day, timeSlot)) {
                JOptionPane.showMessageDialog(null, "이미 예약된 시간대를 포함하고 있습니다.\n예약할 수 없습니다.");
                return;
            }
        }
        String state = "대기";
        RoomReservation reservation = new RoomReservation(
                room, day, periods, roomType, state, user.getName(), user.getRole()
        );
        roomModel.saveReservation(reservation);

        // 예약이 완료된 모든 시간대를 "X"로 변경하고 파일에 바로 저장
        for (String timeSlot : periods) {
            roomModel.markReserved(room, day, timeSlot, jsonPath);
        }

        saveReservationEntry(new ReservationEntry(
                user.getName(),
                user.getRole(),
                roomType,
                room.toString(),
                day,
                periods,
                state
        ));

        JOptionPane.showMessageDialog(null, "예약이 완료되었습니다.");
    }

    private void initListeners() {
        studentView.getBtnStartReservation().addActionListener(e -> {
            String[] options = {"실습실", "일반실"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "예약할 강의실 유형을 선택하세요.",
                    "강의실 유형 선택",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
            if (choice != JOptionPane.CLOSED_OPTION) {
                String jsonPath = (choice == 0) ? "src/Lab_room.json" : "src/normal_room.json";
                String roomType = options[choice];
                startReservation(jsonPath, roomType);
            }
        });

        studentView.getBtnLogout().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "로그아웃하시겠습니까?", "로그아웃", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                studentView.dispose(); // 현재 창 닫기
                new LoginView();         // 로그인 창 열기
            }
        });
    }

    public List<ReservationEntry> loadActiveReservations(String filePath) {
        Gson gson = new Gson();
        List<ReservationEntry> result = new ArrayList<>();
        try (FileReader reader = new FileReader(filePath)) {
            java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<ReservationEntry>>() {}.getType();
            List<ReservationEntry> all = gson.fromJson(reader, listType);
            if (all != null) {
                result.addAll(all);
            }
        } catch (Exception e) {
            // 파일이 없거나 잘못됐을 때는 빈 리스트 반환 (e.printStackTrace(); 로 디버깅 가능)
        }
        return result;
    }

    public void startReservation(String jsonPath, String roomType) {
        List<ReservationEntry> activeLabs = loadActiveReservations("src/Lab_room.json");
        List<ReservationEntry> activeNormals = loadActiveReservations("src/normal_room.json");
        studentView.showReservationUI(jsonPath, roomType);
    }

    public void saveReservationEntry(ReservationEntry entry) {
        String filePath = "reservations.json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            java.io.File file = new java.io.File(filePath);
            java.util.List<ReservationEntry> entries = new java.util.ArrayList<>();
            if (file.exists() && file.length() > 0) {
                try (java.io.FileReader reader = new java.io.FileReader(file)) {
                    ReservationEntry[] array = gson.fromJson(reader, ReservationEntry[].class);
                    if (array != null) {
                        entries = new java.util.ArrayList<>(java.util.Arrays.asList(array));
                    }
                }
            }

            entries.add(entry);

            try (FileWriter writer = new FileWriter(filePath, false)) {
                gson.toJson(entries, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ReservationEntry {
        public String name;
        private String role;
        private String roomType;
        private String roomNumber;
        private String day;
        private List<String> timeSlots;
        public String state;

        public ReservationEntry(String name, String role, String roomType, String roomNumber,
                                String day, List<String> timeSlots, String state) {
            this.name = name;
            this.role = role;
            this.roomType = roomType;
            this.roomNumber = roomNumber;
            this.day = day;
            this.timeSlots = timeSlots;
            this.state = state;
        }
    }

}