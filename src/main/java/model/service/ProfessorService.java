package model.service;

import model.Professor;
import model.RoomReservation;

import java.util.List;

public class ProfessorService {
    private Professor professor;

    public ProfessorService(String name, int class_id) {
        this.professor = new Professor(name, class_id);
    }

    public Professor getUser() {
        return professor;
    }
    public RoomReservation reservation(String roomType, String day, List<String> period){
        return new RoomReservation(roomType, day, period);
    }
}
