package view.professor;

import model.room.RoomModel;
import model.user.User;
import view.login.LoginView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Set;

public class ProfessorView extends JFrame {
    private final JButton btnStartReservation;
    private final JButton btnCancelManageReservation; // [변경] 취소/관리 통합

    private final JButton btnViewReservation;
    private final JButton btnLogout;

    private JComboBox<Integer> roomCombo;
    private JComboBox<String> dayCombo;
    private JList<String> timeSlotList;

    public RoomModel roomModel;
    private String roomType;
    private static User loginUser;

    private static void actionPerformed(ActionEvent e) {
        // 1. 실습실/일반실 선택 다이얼로그 표시
        String[] options = {"실습실", "일반실"};
        int sel = JOptionPane.showOptionDialog(
                null,
                "어떤 종류의 예약을 관리하시겠습니까?",
                "실습실/일반실 선택",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (sel == JOptionPane.CLOSED_OPTION) return;

        String reservationFile;
        String roomJsonPath;
        if (sel == 0) { // 실습실
            reservationFile = "reservations.json";
            roomJsonPath = "src/lab_room.json";
        } else { // 일반실
            reservationFile = "reservations.json";
            roomJsonPath = "src/normal_room.json";
        }

        RoomModel tempRoomModel = new RoomModel(roomJsonPath);

        // 실제 예약취소 관리 View 띄우기 (사용자 정보 등 전달 필요 시 인자 추가!)
        ProfessorRejectedView view = new ProfessorRejectedView(
                reservationFile,
                tempRoomModel,
                roomJsonPath,
                loginUser.getName(),
                loginUser.getRole()
        );
        view.setVisible(true);
    }

    public interface ReservationHandler {
        void onReserve(Integer room, String day, List<String> timeSlots, String roomType);
    }

    private transient ReservationHandler reservationHandler;

    public ProfessorView(User loginUser) {
        this.loginUser = loginUser;

        setTitle("교수 메인 페이지");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 상단: 환영 메시지
        String welcomeMsg = (loginUser != null && loginUser.getName() != null)
                ? loginUser.getName() + " 교수님 안녕하세요"
                : "\"교수님\" 안녕하세요";
        JLabel lblWelcome = new JLabel(welcomeMsg, SwingConstants.CENTER);
        add(lblWelcome, BorderLayout.NORTH);

        // 중앙: 버튼들
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        btnStartReservation = new JButton("예약하기");
        btnCancelManageReservation = new JButton("예약 취소/관리"); // [변경]
        btnViewReservation = new JButton("예약 내역 조회");

        Dimension btnSize = new Dimension(140, 50);
        btnStartReservation.setPreferredSize(btnSize);
        btnCancelManageReservation.setPreferredSize(btnSize);
        btnViewReservation.setPreferredSize(btnSize);

        buttonPanel.add(btnStartReservation);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(btnCancelManageReservation);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(btnViewReservation);

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
        add(buttonPanel, BorderLayout.CENTER);

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLogout = new JButton("로그아웃");
        logoutPanel.add(btnLogout);

        add(logoutPanel, BorderLayout.SOUTH);

        btnViewReservation.addActionListener(e -> {
            controller.professor.ProfessorapprovedController.openUserReservation(loginUser);
        });

        // 로그아웃 액션
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });
        
        btnCancelManageReservation.addActionListener(e -> {
            // (선택창 없이 바로 생성)
            String reservationFile = "reservations.json";
            String labRoomJsonPath = "src/lab_room.json";
            String normalRoomJsonPath = "src/normal_room.json";
            
            RoomModel labRoomModel = new RoomModel(labRoomJsonPath);
            RoomModel normalRoomModel = new RoomModel(normalRoomJsonPath);

            ProfessorRejectedView view = new ProfessorRejectedView(
                    reservationFile,
                    "src/lab_room.json",
                    "src/normal_room.json",
                    loginUser.getName(),
                    loginUser.getRole()
            );
            view.setVisible(true);
        });
        setVisible(true);
    }

    public JButton getBtnStartReservation() {
        return btnStartReservation;
    }
    public JButton getBtnCancelManageReservation() { // [변경]
        return btnCancelManageReservation;
    }
    public JButton getBtnViewReservation() {
        return btnViewReservation;
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

        JButton btnMyReservations = new JButton("예약 내역 조회");
        btnMyReservations.addActionListener(e -> {
            controller.professor.ProfessorapprovedController.openUserReservation(loginUser);
        });

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