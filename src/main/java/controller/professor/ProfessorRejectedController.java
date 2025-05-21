package controller.professor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.professor.ProfessorApprovedListModel;
import model.professor.ProfessorApprovedModel;
import model.user.User;
import view.professor.ProfessorApprovedView;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfessorRejectedController {

    public static void openUserReservation(User user) {
        if (!"PROFESSOR".equals(user.getRole())) {
            System.out.println("교수만 이용 가능합니다.");
            return;
        }

        String jsonFilePath = "reservation.json";

        try {
            ProfessorApprovedListModel model = new ProfessorApprovedListModel(jsonFilePath);
            List<ProfessorApprovedModel> mySchedules = model.getMySchedules(user.getName(), user.getRole());

            SwingUtilities.invokeLater(() -> {
                ProfessorApprovedView view = new ProfessorApprovedView(mySchedules);
                view.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean cancelReservation(
            String reservationFile,
            int roomNumber,
            String userName,
            String userRole,
            List<String> timeSlots,
            String day,
            String RoomType // 실습실 파일
    ) {
        try {
            ProfessorApprovedListModel allList = new ProfessorApprovedListModel(reservationFile);
            boolean success = false;
            for (ProfessorApprovedModel m : allList.getList()) {
                if (m.getRoomNumber() == roomNumber
                        && userName.equals(m.getName())
                        && userRole.equals(m.getRole())
                        && m.getTimeSlots().equals(timeSlots)
                        && day.equals(m.getDay())
                        && ("승인".equals(m.getState()) || "대기".equals(m.getState()))) {
                    m.setState("취소");
                    success = true;
                    break;
                }
            }
            if (success) {
                allList.save(reservationFile);

                String filePath = RoomType;
                if ("normal_room.json".equals(RoomType)) {
                    filePath = "normal_room.json";
                } else if ("Lab_room.json".equals(RoomType)) {
                    filePath = "Lab_room.json";
                }
                if (RoomType != null && RoomType.contains("Lab_room.json") && RoomType != null) {
                    updateLabRoomState(RoomType, roomNumber, day, timeSlots);
                } else if (RoomType != null && !RoomType.contains("normal_room.json") && RoomType != null) {
                    updateRoomState(RoomType, roomNumber, day, timeSlots);
                } else {
                    throw new IllegalArgumentException("알 수 없는 RoomType 입력: " + RoomType);
                }
            }
            return success;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // 실습실(Lab_room.json)용
    private static void updateLabRoomState(String labRoomJsonPath, int roomNumber, String day, List<String> timeSlots) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(labRoomJsonPath));
        JsonNode roomsNode = root.get(0).get("rooms");
        boolean modified = false;
        for (JsonNode roomNode : roomsNode) {
            if (roomNode.get("roomNumber").asInt() == roomNumber) {
                JsonNode dayArray = roomNode.get("schedule").get(day);
                if (dayArray != null && dayArray.isArray()) {
                    for (JsonNode slot : dayArray) {
                        String time = slot.get("time").asText();
                        if (timeSlots.contains(time)) {
                            ((ObjectNode) slot).put("state", "O");
                            modified = true;
                        }
                    }
                }
                break;
            }
        }
        if (modified) {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(labRoomJsonPath), root);
        }
    }

    // 일반실(Room.json)용 - Lab_room.json 구조와 다르다면 여기에 맞춰서 구현
    private static void updateRoomState(String roomJsonPath, int roomNumber, String day, List<String> timeSlots) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(roomJsonPath));
        JsonNode roomsNode = root.get("rooms");
        boolean modified = false;
        for (JsonNode roomNode : roomsNode) {
            if (roomNode.get("roomNumber").asInt() == roomNumber) {
                JsonNode dayArray = roomNode.get("schedule").get(day);
                if (dayArray != null && dayArray.isArray()) {
                    for (JsonNode slot : dayArray) {
                        String time = slot.get("time").asText();
                        if (timeSlots.contains(time)) {
                            ((ObjectNode) slot).put("state", "O");
                            modified = true;
                        }
                    }
                }
                break;
            }
        }
        if (modified) {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(roomJsonPath), root);
        }
    }

    public static List<ProfessorApprovedModel> getMyPendingOrApprovedReservations(
            String reservationFile, String loginUserName, String loginUserRole) throws Exception {
        ProfessorApprovedListModel allList = new ProfessorApprovedListModel(reservationFile);
        List<ProfessorApprovedModel> filtered = new ArrayList<>();
        for (ProfessorApprovedModel m : allList.getList()) {
            if (loginUserRole.equals(m.getRole()) &&
                    loginUserName.equals(m.getName()) &&
                    ("승인".equals(m.getState()) || "대기".equals(m.getState()))) {
                filtered.add(m);
            }
        }
        return filtered;
    }
}