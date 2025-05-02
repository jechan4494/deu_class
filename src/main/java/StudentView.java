package view;

import model.User;
import javax.swing.*;

public class StudentView extends JFrame {
    public StudentView(User user) {
        setTitle("강의실 예약 시스템 - 학생 (" + user.getName() + ")");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // 예약 생성 탭
        tabbedPane.addTab("새 예약", new ReservationForm(user));
        
        // 내 예약 현황 탭
        tabbedPane.addTab("내 예약", new ReservationTable(user));
        
        add(tabbedPane);
    }
}