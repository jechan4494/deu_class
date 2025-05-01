import model.RoomModel;
import view.ProfessorView;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ProfessorView view = new ProfessorView();
        RoomModel roomModel;

        // 실습실 또는 일반실 선택
        String roomType = view.chooseRoomType();

        // 선택한 강의실 종류에 따라 다른 JSON 파일을 로드
        if ("실습실".equals(roomType)) {
            roomModel = new RoomModel("Lab_room.json");
        } else {
            roomModel = new RoomModel("normal_room.json");  // 예시로 일반실 JSON 경로
        }

        // 교수 이름과 학번 입력 받기
        String name = view.getProfessorName();
        int id = view.getProfessorId();

        // 실습실 목록 가져오기
        List<Integer> roomList = roomModel.getRoomNumbers();
        int selectedRoom = view.chooseRoom(roomList);

        // 요일 목록 가져오기
        List<String> days = new ArrayList<>(roomModel.getDays(selectedRoom));
        String chosenDay = view.chooseDay(days);

        // 시간대 목록 가져오기
        List<String> timeSlots = roomModel.getTimeSlots(selectedRoom, chosenDay);
        List<String> chosenSlots = view.chooseTimeSlots(timeSlots);

        // 예약 정보 표시
        model.RoomReservation reservation = new model.RoomReservation(roomType, chosenDay, chosenSlots);
        view.displayReservation(reservation);
    }
}
