import javax.swing.*;
import view.professor.ProfessorView;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProfessorView view = new ProfessorView();

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