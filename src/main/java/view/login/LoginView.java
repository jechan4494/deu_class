package view.login;

import controller.login.AuthController;
import controller.professor.ProfessorController;
import model.login.User;
import view.professor.ProfessorView;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
  private JTextField tfId;
  private JPasswordField pfPassword;
  private JButton btnLogin, btnSignUp;

  public LoginView() {
    setTitle("로그인");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(300, 200);
    setLocationRelativeTo(null);
    setLayout(new GridLayout(4, 2));

    add(new JLabel("아이디:"));
    tfId = new JTextField();
    add(tfId);

    add(new JLabel("비밀번호:"));
    pfPassword = new JPasswordField();
    add(pfPassword);

    btnLogin = new JButton("로그인");
    btnSignUp = new JButton("회원가입");
    add(btnLogin);
    add(btnSignUp);

    // 로그인 버튼 이벤트
    btnLogin.addActionListener(e -> {
      String id = tfId.getText();
      String password = new String(pfPassword.getPassword());
      User user = AuthController.login(id, password);
      if (user != null) {
        JOptionPane.showMessageDialog(this, "로그인 성공! 역할: " + user.getRole());
        // 역할별 화면 이동 (팀원 구현)
        switch (user.getRole()) {
          case "PROFESSOR":
            // 교수 페이지로 이동 (강의실 선택은 그 안에서 수행)
            ProfessorView professorView = new ProfessorView();
            ProfessorController controller = new ProfessorController(professorView, user); // user 객체 전체 전달

            professorView.getBtnStartReservation().addActionListener(e1 -> {
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
                professorView.showReservationUI(jsonPath, roomType);
              }
            });


           /* professorView.addCancelReservationListener(e1 -> {
              JOptionPane.showMessageDialog(null, "예약 취소 기능은 추후 구현 예정입니다.");
            });*/
            break;

          case "STUDENT":
            // new StudentView().setVisible(true);
            break;
          case "TA":
            // new TaView().setVisible(true);
            break;
        }
        dispose();
      } else {
        JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 올바르지 않습니다.");
      }
    });

    // 회원가입 버튼 이벤트
    btnSignUp.addActionListener(e -> {
      dispose();
      new SignUpView().setVisible(true);
    });
  }
}