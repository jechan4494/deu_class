package view.professor;

import model.login.RoomModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class ProfessorView extends JFrame {
    private final JButton btnStartReservation;
    private final JButton btnCancelReservation;

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
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 1));

        btnStartReservation = new JButton("예약하기");
        btnCancelReservation = new JButton("예약 취소하기");

        add(btnStartReservation);
        add(btnCancelReservation);

        setVisible(true);
    }

    public JButton getBtnStartReservation() {
        return btnStartReservation;
    }

    public JButton getBtnCancelReservation() {
        return btnCancelReservation;
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
