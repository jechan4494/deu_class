package controller;

import model.*;
import model.service.ProfessorService;
import view.ProfessorView;

import java.util.ArrayList;
import java.util.List;

public class ProfessorController {
    private ProfessorView professorView;
    private ProfessorService professorService;
    private RoomModel roomModel;

    public ProfessorController(ProfessorView professorView, RoomModel roomModel) {
        this.professorView = professorView;
        this.roomModel = roomModel;
    }

    public void createProfessor(String name, int class_id) {
        professorService = new ProfessorService(name, class_id);
    }
    public void runReservationFlow() {
        if (professorService == null) {
            System.out.println("교수 정보를 먼저 생성하세요.");
            return;
        }

        List<Integer> roomNumbers = roomModel.getRoomNumbers();
        if (roomNumbers.isEmpty()) {
            System.out.println("강의실 정보를 불러올 수 없습니다.");
            return;
        }

        int selectedRoom = professorView.chooseRoom(roomNumbers);

        List<String> days = new ArrayList<>(roomModel.getDays(selectedRoom));
        String selectedDay = professorView.chooseDay(days);

        List<String> timeSlots = roomModel.getTimeSlots(selectedRoom, selectedDay);
        if (timeSlots.isEmpty()) {
            System.out.println("선택한 요일에 가능한 시간이 없습니다.");
            return;
        }

        List<String> chosenSlots = professorView.chooseTimeSlots(timeSlots); // 최대 3개 제한 필요

        RoomReservation reservation = professorService.reservation("실습실", selectedDay, chosenSlots);
        professorView.displayReservation(reservation);
    }

}
