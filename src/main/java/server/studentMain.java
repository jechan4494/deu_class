package server;

import model.user.User;
import view.student.StudentView;
import controller.student.StudentReserveController;

import javax.swing.*;

public class studentMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User testUser = new User("stu01", "pw", "테스트학생", "컴퓨터공학과", "STUDENT");
            StudentView view = new StudentView(testUser);
            new StudentReserveController(view, testUser);
            view.setVisible(true);
        });
    }
}