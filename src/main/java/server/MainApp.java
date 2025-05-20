package server;

import view.login.LoginView;
import javax.swing.*;

// 메인 애플리케이션 클래스
public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
} 