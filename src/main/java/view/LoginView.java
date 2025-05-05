package view;

import controller.AuthController;
import model.User;

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
            // new ProfessorView().setVisible(true);
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
