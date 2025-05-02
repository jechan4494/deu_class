package service;

import model.Reservation;
import model.Room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationService {
    private static final String RESERVATION_FILE = "src/main/resources/data/reservations.json";
    private static final String ROOM_FILE = "src/main/resources/data/rooms.json";
    
    public List<Reservation> getAllReservations() throws IOException {
        return FileService.readData(RESERVATION_FILE, Reservation.class);
    }
    
    public List<Room> getAllRooms() throws IOException {
        return FileService.readData(ROOM_FILE, Room.class);
    }
    
    public List<Reservation> getReservationsByUser(String userId) throws IOException {
        return getAllReservations().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    public boolean isTimeSlotAvailable(String roomId, LocalDateTime start, LocalDateTime end) throws IOException {
        return getAllReservations().stream()
                .filter(r -> r.getRoomId().equals(roomId))
                .noneMatch(r -> !r.getEndTime().isBefore(start) && !r.getStartTime().isAfter(end));
    }
    
    public void createReservation(Reservation reservation) throws IOException {
        List<Reservation> reservations = getAllReservations();
        reservations.add(reservation);
        FileService.writeData(RESERVATION_FILE, reservations);
    }
    
    // 기타 메서드 구현...
}