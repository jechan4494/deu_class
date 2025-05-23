package client.view;

import client.controller.AuthController;
import client.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
  public LoginView(AuthController controller) {
    setTitle("로그인");
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5,5,5,5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JTextField idField = new JTextField(15);
    JPasswordField pwField = new JPasswordField(15);

    gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("아이디:"), gbc);
    gbc.gridx = 1; add(idField, gbc);
    gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("비밀번호:"), gbc);
    gbc.gridx = 1; add(pwField, gbc);

    JButton loginBtn = new JButton("로그인");
    JButton registerBtn = new JButton("회원가입");
    JPanel btnPanel = new JPanel();
    btnPanel.add(loginBtn);
    btnPanel.add(registerBtn);

    gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; add(btnPanel, gbc);

    loginBtn.addActionListener(e -> {
      String id = idField.getText().trim();
      String pw = new String(pwField.getPassword()).trim();
      User user = controller.login(id, pw);
      if (user != null) {
        JOptionPane.showMessageDialog(this, user.getRole() + " 로그인 성공!");
        // 역할별 화면 분기(팀원 구현)
        dispose();
      } else {
        JOptionPane.showMessageDialog(this, "로그인 실패!");
      }
    });

    registerBtn.addActionListener(e -> {
      // 회원가입 창을 띄우고, 로그인 창은 잠깐 숨김
      this.setVisible(false);
      new RegisterView(controller, this);
    });

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(350, 200);
    setLocationRelativeTo(null);
    setVisible(true);
  }
}
