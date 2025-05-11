package controller.professor;

import com.google.gson.Gson;
import model.login.RoomReservation;
import model.login.User;
import view.professor.ProfessorView;

import javax.swing.*;
import java.io.FileWriter;
import java.util.List;

public class ProfessorController {
    private final ProfessorView professorView;
    private final User user;

    public ProfessorController(ProfessorView professorView, User user) {
        this.professorView = professorView;
        this.user = user;

        professorView.setReservationHandler(this::reserveRoom);
        initListeners();
    }

    private void reserveRoom(Integer room, String day, List<String> periods, String roomType) {
        String state = "대기";

        RoomReservation reservation = new RoomReservation(
                room, day, periods, roomType, state, user.getName(), user.getRole()
        );
        professorView.roomModel.saveReservation(reservation);

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
        professorView.getBtnStartReservation().addActionListener(e -> {
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
                String jsonPath = (choice == 0) ? "Lab_room.json" : "normal_room.json";
                String roomType = options[choice];
                startReservation(jsonPath, roomType);
            }
        });

        professorView.getBtnCancelReservation().addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "예약 취소 기능은 아직 구현되지 않았습니다.");
        });
    }

    public void startReservation(String jsonPath, String roomType) {
        professorView.showReservationUI(jsonPath, roomType);
    }

    private void saveReservationEntry(ReservationEntry entry) {
        try (FileWriter writer = new FileWriter("reservations.json", true)) {
            String json = new Gson().toJson(entry);
            writer.write(json + System.lineSeparator());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ReservationEntry {
        private String name;
        private String role;
        private String roomType;
        private String roomNumber;
        private String day;
        private List<String> timeSlots;
        private String state;

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
