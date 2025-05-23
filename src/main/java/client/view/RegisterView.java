package client.view;

import client.controller.AuthController;
import client.model.User;

import javax.swing.*;
import java.awt.*;

public class RegisterView extends JFrame {
  public RegisterView(AuthController controller, JFrame loginView) {
    setTitle("회원가입");
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5,5,5,5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JTextField idField = new JTextField(15);
    JPasswordField pwField = new JPasswordField(15);
    JTextField nameField = new JTextField(15);
    JTextField deptField = new JTextField(15);
    JComboBox<String> roleBox = new JComboBox<>(new String[]{"교수", "학생", "조교"});

    gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("아이디:"), gbc);
    gbc.gridx = 1; add(idField, gbc);
    gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("비밀번호:"), gbc);
    gbc.gridx = 1; add(pwField, gbc);
    gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("이름:"), gbc);
    gbc.gridx = 1; add(nameField, gbc);
    gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("학과:"), gbc);
    gbc.gridx = 1; add(deptField, gbc);
    gbc.gridx = 0; gbc.gridy = 4; add(new JLabel("역할:"), gbc);
    gbc.gridx = 1; add(roleBox, gbc);

    JButton registerBtn = new JButton("회원가입");
    gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
    add(registerBtn, gbc);

    registerBtn.addActionListener(e -> {
      String id = idField.getText().trim();
      String pw = new String(pwField.getPassword()).trim();
      String name = nameField.getText().trim();
      String dept = deptField.getText().trim();
      String role = (String) roleBox.getSelectedItem();

      if (id.isEmpty() || pw.isEmpty() || name.isEmpty() || dept.isEmpty()) {
        JOptionPane.showMessageDialog(this, "모든 항목을 입력하세요.");
        return;
      }

      User user = new User(id, pw, name, dept, role);
      String result = controller.register(user);
      JOptionPane.showMessageDialog(this, result);

      if ("회원가입 성공!".equals(result)) {
        // 회원가입 성공 시: 현재 창 닫고 로그인 창 보이게
        this.dispose();
        if (loginView != null) loginView.setVisible(true);
      }
    });

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setSize(350, 320);
    setLocationRelativeTo(null);
    setVisible(true);
  }
}
