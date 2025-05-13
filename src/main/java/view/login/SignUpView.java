package view.login;

import controller.login.AuthController;
import model.user.User;

import javax.swing.*;
import java.awt.*;

public class SignUpView extends JFrame {
  private JTextField tfId, tfName, tfDepartment;
  private JPasswordField pfPassword;
  private JComboBox<String> cbRole;
  private JButton btnCheckId, btnSignUp;

  public SignUpView() {
    setTitle("회원가입");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setSize(400, 350);
    setLocationRelativeTo(null);

    try {
      for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          SwingUtilities.updateComponentTreeUI(this);
          break;
        }
      }
    } catch (Exception e) {
    }

    JPanel panel = new JPanel();
    panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
    panel.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    gbc.gridx = 0; gbc.gridy = 0;
    panel.add(new JLabel("아이디"), gbc);
    gbc.gridx = 1;
    tfId = new JTextField();
    tfId.setPreferredSize(new Dimension(200, 30)); // 입력칸 넓게
    gbc.weightx = 1.5; // <<< 추가: 가로 공간을 더 많이 차지
    panel.add(tfId, gbc);

    gbc.weightx = 0; // 다음 컴포넌트에 영향 안 주게 초기화

    gbc.gridx = 2;
    btnCheckId = new JButton("중복확인");
    panel.add(btnCheckId, gbc);

    gbc.gridx = 0; gbc.gridy = 1;
    panel.add(new JLabel("비밀번호"), gbc);
    gbc.gridx = 1; gbc.gridwidth = 2;
    pfPassword = new JPasswordField();
    pfPassword.setPreferredSize(new Dimension(200, 30));
    panel.add(pfPassword, gbc);
    gbc.gridwidth = 1;

    gbc.gridx = 0; gbc.gridy = 2;
    panel.add(new JLabel("이름"), gbc);
    gbc.gridx = 1; gbc.gridwidth = 2;
    tfName = new JTextField();
    tfName.setPreferredSize(new Dimension(200, 30));
    panel.add(tfName, gbc);
    gbc.gridwidth = 1;

    gbc.gridx = 0; gbc.gridy = 3;
    panel.add(new JLabel("학과"), gbc);
    gbc.gridx = 1; gbc.gridwidth = 2;
    tfDepartment = new JTextField();
    tfDepartment.setPreferredSize(new Dimension(200, 30));
    panel.add(tfDepartment, gbc);
    gbc.gridwidth = 1;

    gbc.gridx = 0; gbc.gridy = 4;
    panel.add(new JLabel("역할"), gbc);
    gbc.gridx = 1; gbc.gridwidth = 2;
    String[] roles = {"학생", "교수", "조교"};
    cbRole = new JComboBox<>(roles);
    panel.add(cbRole, gbc);
    gbc.gridwidth = 1;

    gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 2;
    btnSignUp = new JButton("회원가입");
    panel.add(btnSignUp, gbc);

    add(panel);

    btnCheckId.addActionListener(e -> {
      String id = tfId.getText();
      if (AuthController.checkDuplicateId(id)) {
        JOptionPane.showMessageDialog(this, "중복된 아이디가 있습니다.");
      } else {
        JOptionPane.showMessageDialog(this, "사용 가능한 아이디입니다.");
      }
    });

    btnSignUp.addActionListener(e -> {
      String id = tfId.getText();
      String password = new String(pfPassword.getPassword());
      String name = tfName.getText();
      String department = tfDepartment.getText();
      String roleKor = (String)cbRole.getSelectedItem();

      String roleEng = switch (roleKor) {
        case "학생" -> "STUDENT";
        case "교수" -> "PROFESSOR";
        case "조교" -> "TA";
        default -> "STUDENT";
      };

      if (id.isEmpty() || password.isEmpty() || name.isEmpty() || department.isEmpty()) {
        JOptionPane.showMessageDialog(this, "모든 정보를 입력해주세요.");
        return;
      }

      User newUser = new User(id, password, name, department, roleEng);
      if (AuthController.registerUser(newUser)) {
        dispose();
        new LoginView().setVisible(true);
      }
    });
  }
}