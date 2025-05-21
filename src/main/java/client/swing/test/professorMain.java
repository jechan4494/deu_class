package client.swing.test;

import server.model.user.User;
import client.view.professor.ProfessorView;
import server.controller.professor.ProfessorReserveController;

import javax.swing.*;

public class professorMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User testUser = new User("prof01", "pw", "테스트교수", "컴퓨터공학과", "PROFESSOR");
            ProfessorView view = new ProfessorView(testUser);
            new ProfessorReserveController(view, testUser);
        });
    }
}