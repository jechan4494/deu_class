package server;

import javax.swing.*;

import model.user.User;
import view.professor.ProfessorView;

public class professorMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User testUser = new User("prof01", "pw", "테스트교수", "컴퓨터공학과", "PROFESSOR");
            ProfessorView view = new ProfessorView(testUser);


            String[] options = {"실습실", "일반실"};
            int choice = JOptionPane.showOptionDialog(null, "어떤 강의실을 예약하시겠습니까?",
                    "강의실 선택", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            // 0을 선택하면 "Lab_room.json", 1을 선택하면 "normal_room.json"을 넘김
            if (choice == 0) {
                view.showReservationUI("src/Lab_room.json", "실습실");
            } else if (choice == 1) {
                view.showReservationUI("src/normal_room.json", "일반실");
            }
        });
    }
}