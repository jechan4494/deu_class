package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.common.Reservation;
import service.ReservationService;
import model.room.RoomModel;
import model.user.User;
import model.user.UserModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReservationServer implements ReservationService {
    private static final Logger LOGGER = Logger.getLogger(ReservationServer.class.getName());
    private final String reservationFile = "src/main/resources/reservations.json";
    private final String userFile = "src/main/resources/users.json";
    private final String logFile = "src/main/resources/ta_log.json";
    private final Gson gson;
    private final RoomModel normalRoomModel;
    private final RoomModel labRoomModel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ReservationServer() {
        this.gson = new Gson();
        this.normalRoomModel = new RoomModel("src/main/resources/room_data.json");
        this.labRoomModel = new RoomModel("src/main/resources/room_data.json");
    }

    @Override
    public List<User> getAllUsers() {
        try {
            File file = new File(userFile);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            try (Reader reader = new FileReader(file)) {
                Type type = new TypeToken<List<User>>(){}.getType();
                return gson.fromJson(reader, type);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading user data: " + e.getMessage(), e);
            System.err.println("사용자 데이터 로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean registerUser(User user) {
        try {
            List<User> users = getAllUsers();
            if (users.stream().anyMatch(u -> u.getName().equals(user.getName()))) {
                return false;
            }
            users.add(user);
            saveUsers(users);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error registering user: " + e.getMessage(), e);
            System.err.println("사용자 등록 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean makeReservation(Reservation reservation) {
        try {
            if (!isRoomAvailable(reservation.getRoomNumber(), reservation.getDay(), 
                               reservation.getTimeSlots(), reservation.getRoomType())) {
                return false;
            }

            List<Reservation> reservations = loadReservations();
            reservations.add(reservation);
            saveReservations(reservations);

            RoomModel roomModel = "실습실".equals(reservation.getRoomType()) ? labRoomModel : normalRoomModel;
            for (String timeSlot : reservation.getTimeSlots()) {
                roomModel.markReserved(reservation.getRoomNumber(), reservation.getDay(), 
                                     timeSlot, roomModel.getJsonPath());
            }

            // 예약 로그 추가
            addLog(reservation.getName(), 
                  reservation.getRole(), 
                  "예약",
                  String.format("%s %d호 %s %s 예약 %s", 
                      reservation.getRoomType(),
                      reservation.getRoomNumber(),
                      reservation.getDay(),
                      String.join(", ", reservation.getTimeSlots()),
                      reservation.getState()));

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing reservation: " + e.getMessage(), e);
            System.err.println("예약 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Reservation> getUserReservations(String name, String role) {
        try {
            List<Reservation> reservations = loadReservations();
            return reservations.stream()
                    .filter(r -> r.getName().equals(name) && r.getRole().equals(role))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving user reservations: " + e.getMessage(), e);
            System.err.println("사용자 예약 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean cancelReservation(Reservation reservation) {
        try {
            List<Reservation> reservations = loadReservations();
            boolean found = false;
            
            for (Reservation r : reservations) {
                if (r.getName().equals(reservation.getName()) &&
                    r.getRole().equals(reservation.getRole()) &&
                    r.getRoomNumber() == reservation.getRoomNumber() &&
                    r.getDay().equals(reservation.getDay()) &&
                    r.getTimeSlots().equals(reservation.getTimeSlots()) &&
                    r.getRoomType().equals(reservation.getRoomType())) {
                    r.setState("취소");
                    found = true;
                    break;
                }
            }

            if (found) {
                saveReservations(reservations);
                
                RoomModel roomModel = "실습실".equals(reservation.getRoomType()) ? labRoomModel : normalRoomModel;
                for (String timeSlot : reservation.getTimeSlots()) {
                    roomModel.markCancelled(reservation.getRoomNumber(), reservation.getDay(), 
                                          timeSlot, roomModel.getJsonPath());
                }

                // 취소 로그 추가
                addLog(reservation.getName(), 
                      reservation.getRole(), 
                      "예약 취소",
                      String.format("%s %d호 %s %s 예약 취소", 
                          reservation.getRoomType(),
                          reservation.getRoomNumber(),
                          reservation.getDay(),
                          String.join(", ", reservation.getTimeSlots())));

                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cancelling reservation: " + e.getMessage(), e);
            System.err.println("예약 취소 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Reservation> getAllReservations() {
        try {
            return loadReservations();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving reservations: " + e.getMessage(), e);
            System.err.println("예약 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean updateReservationState(Reservation reservation, String newState) {
        try {
            List<Reservation> reservations = loadReservations();
            boolean found = false;
            
            for (Reservation r : reservations) {
                if (r.getName().equals(reservation.getName()) &&
                    r.getRole().equals(reservation.getRole()) &&
                    r.getRoomNumber() == reservation.getRoomNumber() &&
                    r.getDay().equals(reservation.getDay()) &&
                    r.getTimeSlots().equals(reservation.getTimeSlots()) &&
                    r.getRoomType().equals(reservation.getRoomType())) {
                    r.setState(newState);
                    found = true;
                    break;
                }
            }

            if (found) {
                saveReservations(reservations);
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating reservation state: " + e.getMessage(), e);
            System.err.println("예약 상태 업데이트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isRoomAvailable(int roomNumber, String day, List<String> timeSlots, String roomType) {
        RoomModel roomModel = "실습실".equals(roomType) ? labRoomModel : normalRoomModel;
        for (String timeSlot : timeSlots) {
            if (!roomModel.isReservable(roomNumber, day, timeSlot)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean updateUser(User user) {
        try {
            List<User> users = getAllUsers();
            boolean found = false;
            
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getName().equals(user.getName())) {
                    users.set(i, user);
                    found = true;
                    break;
                }
            }

            if (found) {
                saveUsers(users);
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating user: " + e.getMessage(), e);
            System.err.println("사용자 정보 업데이트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteUser(String userId) {
        try {
            List<User> users = getAllUsers();
            boolean removed = users.removeIf(user -> user.getName().equals(userId));
            
            if (removed) {
                saveUsers(users);
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting user: " + e.getMessage(), e);
            System.err.println("사용자 삭제 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getLogs(String userType) {
        try {
            List<Map<String, Object>> allLogs = loadLogs();
            if (userType == null || userType.isEmpty()) {
                return allLogs;
            }
            return allLogs.stream()
                .filter(log -> userType.equalsIgnoreCase((String) log.get("userType")))
                .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting logs: " + e.getMessage(), e);
            System.err.println("로그 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Reservation> loadReservations() throws IOException {
        File file = new File(reservationFile);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<List<Reservation>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading reservation data: " + e.getMessage(), e);
            System.err.println("예약 데이터 로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveReservations(List<Reservation> reservations) throws IOException {
        try (Writer writer = new FileWriter(reservationFile)) {
            gson.toJson(reservations, writer);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving reservation data: " + e.getMessage(), e);
            System.err.println("예약 데이터 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveUsers(List<User> users) throws IOException {
        try (Writer writer = new FileWriter(userFile)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving user data: " + e.getMessage(), e);
            System.err.println("사용자 데이터 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addLog(String user, String userType, String action, String details) {
        try {
            List<Map<String, Object>> logs = loadLogs();
            Map<String, Object> newLog = new HashMap<>();
            newLog.put("timestamp", LocalDateTime.now().format(dateFormatter));
            newLog.put("user", user);
            newLog.put("userType", userType);
            newLog.put("action", action);
            newLog.put("details", details);
            logs.add(newLog);
            saveLogs(logs);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding log: " + e.getMessage(), e);
            System.err.println("로그 추가 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Map<String, Object>> loadLogs() throws IOException {
        File file = new File(logFile);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<List<Map<String, Object>>>(){}.getType();
            return gson.fromJson(reader, type);
        }
    }

    private void saveLogs(List<Map<String, Object>> logs) throws IOException {
        try (Writer writer = new FileWriter(logFile)) {
            gson.toJson(logs, writer);
        }
    }
} 