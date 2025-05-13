package view.professor;

import model.room.RoomModel;
import view.login.LoginView;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class ProfessorView extends JFrame {
    private final JButton btnStartReservation;
    private final JButton btnCancelReservation;
    private final JButton btnLogout;

    private JComboBox<Integer> roomCombo;
    private JComboBox<String> dayCombo;
    private JList<String> timeSlotList;

    public RoomModel roomModel;
    private String roomType;

    public interface ReservationHandler {
        void onReserve(Integer room, String day, List<String> timeSlots, String roomType);
    }

    private transient ReservationHandler reservationHandler;

    public ProfessorView() {
        setTitle("교수 메인 페이지");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 상단: 환영 메시지
        JLabel lblWelcome = new JLabel("\"교수님\" 안녕하세요", SwingConstants.CENTER);
        add(lblWelcome, BorderLayout.NORTH);

// 중앙: 버튼 2개를 가로로 좌우에 배치
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        btnStartReservation = new JButton("예약하기");
        btnCancelReservation = new JButton("예약 취소하기");

        Dimension btnSize = new Dimension(180, 60);
        btnStartReservation.setPreferredSize(btnSize);
        btnStartReservation.setMaximumSize(btnSize);
        btnStartReservation.setMinimumSize(btnSize);
        btnCancelReservation.setPreferredSize(btnSize);
        btnCancelReservation.setMaximumSize(btnSize);
        btnCancelReservation.setMinimumSize(btnSize);

        // 좌우로 벌어지도록 수평 글루 추가
        buttonPanel.add(btnStartReservation);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(btnCancelReservation);

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30)); // 상하좌우 여백

        add(buttonPanel, BorderLayout.CENTER);

        // 아래쪽: 로그아웃 버튼 (오른쪽 정렬)
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLogout = new JButton("로그아웃");
        logoutPanel.add(btnLogout);

        add(logoutPanel, BorderLayout.SOUTH);

        Dimension btnDim = new Dimension(240, 50);
        btnStartReservation.setPreferredSize(btnDim);
        btnCancelReservation.setPreferredSize(btnDim);
        btnLogout.setPreferredSize(btnDim);

        // 로그아웃 액션
        btnLogout.addActionListener(e -> {
            dispose(); // 현재 창 닫기
            new LoginView().setVisible(true); // 로그인 창 다시 열기
        });

        setVisible(true);
    }

    public JButton getBtnStartReservation() {
        return btnStartReservation;
    }

    public JButton getBtnCancelReservation() {
        return btnCancelReservation;
    }

    public JButton getBtnLogout() {
        return btnLogout;
    }

    public void setReservationHandler(ReservationHandler handler) {
        this.reservationHandler = handler;
    }

    public void showReservationUI(String jsonPath, String roomType) {
        this.roomType = roomType;
        this.roomModel = new RoomModel(jsonPath);

        if (roomModel == null || roomModel.getRoomNumbers().isEmpty()) {
            JOptionPane.showMessageDialog(this, "예약 가능한 강의실이 없습니다.");
            return;
        }

        JFrame reservationFrame = new JFrame("강의실 예약 화면");
        reservationFrame.setSize(400, 400);
        reservationFrame.setLayout(new GridLayout(5, 1));

        roomCombo = new JComboBox<>(roomModel.getRoomNumbers().toArray(new Integer[0]));
        roomCombo.addActionListener(e -> updateDayCombo());
        reservationFrame.add(labeledPanel("강의실 선택:", roomCombo));

        dayCombo = new JComboBox<>();
        dayCombo.addActionListener(e -> updateTimeSlots());
        reservationFrame.add(labeledPanel("요일 선택:", dayCombo));

        timeSlotList = new JList<>();
        timeSlotList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        reservationFrame.add(labeledPanel("시간대 선택:", new JScrollPane(timeSlotList)));

        JButton btnReserve = new JButton("예약하기");
        btnReserve.addActionListener(e -> handleReservation());
        reservationFrame.add(btnReserve);

        updateDayCombo();
        reservationFrame.setVisible(true);
    }

    private JPanel labeledPanel(String label, Component component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(label), BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private void updateDayCombo() {
        Integer room = (Integer) roomCombo.getSelectedItem();
        if (room == null) return;

        Set<String> days = roomModel.getDays(room);
        dayCombo.removeAllItems();
        for (String day : days) dayCombo.addItem(day);

        updateTimeSlots();
    }

    private void updateTimeSlots() {
        Integer room = (Integer) roomCombo.getSelectedItem();
        String day = (String) dayCombo.getSelectedItem();
        if (room == null || day == null) return;

        List<String> slots = roomModel.getTimeSlots(room, day);
        timeSlotList.setListData(slots.toArray(new String[0]));
    }

    private void handleReservation() {
        Integer room = (Integer) roomCombo.getSelectedItem();
        if (room == null) {
            JOptionPane.showMessageDialog(this, "강의실을 선택해주세요.");
            return;
        }

        String day = (String) dayCombo.getSelectedItem();
        List<String> selectedSlots = timeSlotList.getSelectedValuesList();

        if (selectedSlots.size() > 3) {
            JOptionPane.showMessageDialog(this, "최대 3개의 시간대만 선택할 수 있습니다.");
            return;
        }

        if (reservationHandler != null) {
            reservationHandler.onReserve(room, day, selectedSlots, roomType);
        }
    }
}