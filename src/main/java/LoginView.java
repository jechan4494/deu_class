package view;

import controller.UserController;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;
    private UserController userController;
    
    public LoginView() {
        this.userController = new UserController();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("강의실 예약 시스템 - 로그인");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // ID 입력 필드
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("아이디:"), gbc);
        
        gbc.gridx = 1;
        idField = new JTextField(15);
        panel.add(idField, gbc);
        
        // 비밀번호 입력 필드
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("비밀번호:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);
        
        // 로그인 버튼
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("로그인");
        loginButton.addActionListener(e -> attemptLogin());
        panel.add(loginButton, gbc);
        
        add(panel);
    }
    
    private void attemptLogin() {
        String id = idField.getText();
        String password = new String(passwordField.getPassword());
        
        try {
            User user = userController.authenticate(id, password);
            if (user != null) {
                openUserDashboard(user);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "잘못된 아이디 또는 비밀번호입니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "로그인 처리 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void openUserDashboard(User user) {
        switch (user.getRole()) {
            case ADMIN:
                new AdminView(user).setVisible(true);
                break;
            case PROFESSOR:
                new ProfessorView(user).setVisible(true);
                break;
            case STUDENT:
                new StudentView(user).setVisible(true);
                break;
        }
    }
}