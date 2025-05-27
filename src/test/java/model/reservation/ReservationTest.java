package model.reservation;

import model.common.Reservation;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ReservationTest {
    
    @Test
    public void testStudentReservationCreation() {
        List<String> timeSlots = Arrays.asList("10:00", "11:00");
        Reservation reservation = new Reservation("테스트학생", "student", "일반강의실", 101, "2024-05-27", timeSlots, "대기중");
        
        assertEquals("테스트학생", reservation.getName());
        assertEquals("student", reservation.getRole());
        assertEquals("일반강의실", reservation.getRoomType());
        assertEquals(101, reservation.getRoomNumber());
        assertEquals("2024-05-27", reservation.getDay());
        assertEquals(timeSlots, reservation.getTimeSlots());
        assertEquals("대기중", reservation.getState());
    }
    
    @Test
    public void testProfessorReservationCreation() {
        List<String> timeSlots = Arrays.asList("14:00", "15:00", "16:00");
        Reservation reservation = new Reservation("테스트교수", "professor", "실습실", 201, "2024-05-28", timeSlots, "승인");
        
        assertEquals("테스트교수", reservation.getName());
        assertEquals("professor", reservation.getRole());
        assertEquals("실습실", reservation.getRoomType());
        assertEquals(201, reservation.getRoomNumber());
        assertEquals("2024-05-28", reservation.getDay());
        assertEquals(timeSlots, reservation.getTimeSlots());
        assertEquals("승인", reservation.getState());
    }
    
    @Test
    public void testTAReservationCreation() {
        List<String> timeSlots = Arrays.asList("09:00", "10:00");
        Reservation reservation = new Reservation("테스트조교", "ta", "실습실", 301, "2024-05-29", timeSlots, "승인");
        
        assertEquals("테스트조교", reservation.getName());
        assertEquals("ta", reservation.getRole());
        assertEquals("실습실", reservation.getRoomType());
        assertEquals(301, reservation.getRoomNumber());
        assertEquals("2024-05-29", reservation.getDay());
        assertEquals(timeSlots, reservation.getTimeSlots());
        assertEquals("승인", reservation.getState());
    }
    
    @Test
    public void testReservationStateChange() {
        List<String> timeSlots = Arrays.asList("10:00", "11:00");
        Reservation reservation = new Reservation("테스트학생", "student", "일반강의실", 101, "2024-05-27", timeSlots, "대기중");
        
        reservation.setState("승인");
        assertEquals("승인", reservation.getState());
        
        reservation.setState("거절");
        assertEquals("거절", reservation.getState());
    }
    
    @Test
    public void testTimeSlotModification() {
        List<String> initialTimeSlots = Arrays.asList("10:00", "11:00");
        Reservation reservation = new Reservation("테스트학생", "student", "일반강의실", 101, "2024-05-27", initialTimeSlots, "대기중");
        
        List<String> newTimeSlots = Arrays.asList("13:00", "14:00", "15:00");
        reservation.setTimeSlots(newTimeSlots);
        
        assertEquals(newTimeSlots, reservation.getTimeSlots());
        assertEquals(3, reservation.getTimeSlots().size());
    }

    @Test
    public void testProfessorAutoApproval() {
        List<String> timeSlots = Arrays.asList("10:00", "11:00");
        Reservation reservation = new Reservation("테스트교수", "professor", "일반강의실", 101, "2024-05-27", timeSlots, "대기중");
        
        // 교수의 예약은 자동으로 승인 상태가 되어야 함
        assertEquals("승인", reservation.getState());
    }

    @Test
    public void testTAReservationManagement() {
        // 조교가 관리하는 예약 생성
        List<String> timeSlots = Arrays.asList("10:00", "11:00");
        Reservation studentReservation = new Reservation("테스트학생", "student", "일반강의실", 101, "2024-05-27", timeSlots, "대기중");
        
        // 조교의 예약 상태 변경 권한 테스트
        studentReservation.setState("승인");
        assertEquals("승인", studentReservation.getState());
        
        studentReservation.setState("거절");
        assertEquals("거절", studentReservation.getState());
    }
} 