package view.login;

import controller.login.AuthController;
import controller.professor.ProfessorController;
import model.user.*;
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
        // 역할별 화면 이동
        switch (user.getRole()) {
          case "PROFESSOR":
            // 교수 메인 뷰로 전환
            ProfessorView professorView = new ProfessorView();
            ProfessorController controller = new ProfessorController(professorView, user); // 교수 컨트롤러 연결

            // (이벤트: 예약하기 버튼 클릭 시 강의실 종류 선택 및 UI 표시)
            professorView.getBtnStartReservation().addActionListener(e1 -> {
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
                String jsonPath = (choice == 0) ? "src/Lab_room.json" : "src/normal_room.json";
                String roomType = options[choice];
                professorView.showReservationUI(jsonPath, roomType);
              }
            });

            professorView.setVisible(true); // 교수 뷰를 보이게 함
            break;

          case "STUDENT":
            // 학생용 화면 전환 코드 (구현 시 추가)
            break;
          case "TA":
            // 조교용 화면 전환 코드 (구현 시 추가)
            break;
        }
        dispose(); // 로그인 창 닫기
      } else {
        JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 올바르지 않습니다.");
      }
    });

    // 회원가입 버튼 이벤트
    btnSignUp.addActionListener(e -> {
      dispose();
      new SignUpView().setVisible(true);
    });
    setVisible(true);
  }
}