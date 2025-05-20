package controller.professor;

import model.professor.ProfessorApprovedModel;
import model.professor.ProfessorApprovedListModel;
import model.room.RoomModel;

import java.util.List;

public class ProfessorRejectedController {

    public static boolean cancelReservation(
            int reservationId,
            String reservationFile,
            RoomModel roomModel,
            String roomJsonPath
    ) {
        // 예약 리스트를 바로 가져와서 처리
        ProfessorApprovedListModel reservationList = null;
        try {
            reservationList = new ProfessorApprovedListModel(reservationFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<ProfessorApprovedModel> list = reservationList.getList();

        for (ProfessorApprovedModel model : list) {
            if (model.getRoomNumber() == reservationId && "대기".equals(model.getState())) {
                // 상태 변경
                model.setState("취소");

                // 방 상태 복구
                int roomNo = model.getRoomNumber();
                String day = model.getDay();
                List<String> timeSlots = model.getTimeSlots();

                for (String time : timeSlots) {
                    roomModel.markCancelled(roomNo, day, time, roomJsonPath);
                }

                try {
                    reservationList.save(reservationFile); // 변경사항 저장
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        }
        return false;
    }

public static boolean cancelMyReservation(
        String loginUserName,
        String loginUserRole,
        String reservationFile,
        RoomModel roomModel,
        String roomJsonPath // 예: "normal_room.json"
) {
    ProfessorApprovedListModel reservationList;
    try {
        reservationList = new ProfessorApprovedListModel(reservationFile);
    } catch (Exception e) {
        throw new RuntimeException("예약 목록을 불러오는 데 실패했습니다.", e);
    }

    // 1. 내 예약만 조회 (승인 상태만)
    List<ProfessorApprovedModel> myReservations = reservationList.getMySchedules(loginUserName, loginUserRole)
            .stream()
            .filter(r -> "승인".equals(r.getState()))
            .toList();

    if (myReservations.isEmpty()) {
        System.out.println("취소할 수 있는 예약이 없습니다.");
        return false;
    }

    // 2. 사용자에게 목록을 보여주고, 취소할 예약을 선택한다고 가정(여기선 첫 번째 내역을 취소)
    ProfessorApprovedModel toCancel = myReservations.get(0); // 실제로는 사용자 입력에 따라 선택

    toCancel.setState("취소");

    // 4. room 상태 복원
    if ("일반실".equals(toCancel.getRoomType()) || "실습실".equals(toCancel.getRoomType())) {
        int roomNo = toCancel.getRoomNumber();
        String day = toCancel.getDay();
        List<String> timeSlots = toCancel.getTimeSlots();
        for (String time : timeSlots) {
            roomModel.markCancelled(roomNo, day, time, roomJsonPath);
        }
    }

    // 5. 변경된 예약 내역을 파일에 저장
    try {
        reservationList.save(reservationFile);
    } catch (Exception e) {
        throw new RuntimeException("예약 내역 저장에 실패했습니다.", e);
    }
    return true;
}
}