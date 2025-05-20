package view.login;

import client.ServerConnector;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SignUpView extends JFrame {
  private JTextField tfId, tfName, tfDepartment;
  private JPasswordField pfPassword;
  private JComboBox<String> cbRole;
  private JButton btnSignUp, btnCancel;

  public SignUpView() {
    setTitle("회원가입");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setSize(400, 300);
    setLocationRelativeTo(null);
    setLayout(new GridLayout(6, 2, 10, 10));

    add(new JLabel("아이디:"));
    tfId = new JTextField();
    add(tfId);

    add(new JLabel("비밀번호:"));
    pfPassword = new JPasswordField();
    add(pfPassword);

    add(new JLabel("이름:"));
    tfName = new JTextField();
    add(tfName);

    add(new JLabel("학과:"));
    tfDepartment = new JTextField();
    add(tfDepartment);

    add(new JLabel("역할:"));
    cbRole = new JComboBox<>(new String[]{"STUDENT", "PROFESSOR", "TA"});
    add(cbRole);

    btnSignUp = new JButton("회원가입");
    btnCancel = new JButton("취소");
    add(btnSignUp);
    add(btnCancel);

    btnSignUp.addActionListener(e -> {
      String id = tfId.getText().trim();
      String pw = new String(pfPassword.getPassword()).trim();
      String name = tfName.getText().trim();
      String dept = tfDepartment.getText().trim();
      String role = (String) cbRole.getSelectedItem();

      if (id.isEmpty() || pw.isEmpty() || name.isEmpty() || dept.isEmpty()) {
        JOptionPane.showMessageDialog(this, "모든 항목을 입력하세요.");
        return;
      }

      Map<String, String> resp = ServerConnector.signup(id, pw, name, dept, role);
      if (resp == null) {
        JOptionPane.showMessageDialog(this, "서버 연결에 실패했습니다. 서버가 실행 중인지 관리자에게 문의하세요.");
      } else if ("success".equals(resp.get("result"))) {
        JOptionPane.showMessageDialog(this, "회원가입 성공!");
        dispose();
        new LoginView().setVisible(true);
      } else if ("fail".equals(resp.get("result"))) {
        JOptionPane.showMessageDialog(this, "이미 존재하는 아이디입니다.");
      } else {
        JOptionPane.showMessageDialog(this, "알 수 없는 오류");
      }
    });

    btnCancel.addActionListener(e -> {
      dispose();
      new LoginView().setVisible(true);
    });
  }
}