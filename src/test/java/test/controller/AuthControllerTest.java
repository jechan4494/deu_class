package test.controller;

import controller.professor.ProfessorReserveController;
import model.user.User;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

// DummyProfessorView에서 스윙 사용하지 않음
class DummyProfessorViewTest extends view.professor.ProfessorView {
  public DummyProfessorViewTest(model.user.User loginUser) { super(loginUser); }
  public void setReservationHandler(java.util.function.Consumer<java.util.List<String>> handler) {}
}

public class AuthControllerTest {
  ProfessorReserveController.ReservationEntry sampleEntry;
  String tempFile;

  @BeforeEach
  void setup() throws IOException {
    sampleEntry = new ProfessorReserveController.ReservationEntry(
            "홍길동", "교수", "강의실", "101", "월", Arrays.asList("09:00-10:00", "10:00-11:00"), "대기");
    // 임시 파일 생성
    File f = File.createTempFile("reservations_test_", ".json");
    f.deleteOnExit();
    tempFile = f.getAbsolutePath();
  }

  @Test
  void saveAndLoadReservationEntry_테스트() {
    User dummyUser = new User("P123", "1234", "정찬", "컴소", "교수");
    DummyProfessorViewTest dummyView = new DummyProfessorViewTest(dummyUser);
    ProfessorReserveController controller = new ProfessorReserveController(dummyView, dummyUser);

    controller.saveReservationEntry(sampleEntry);

    List<ProfessorReserveController.ReservationEntry> loaded =
            controller.loadActiveReservations("reservations.json"); // 실제 메서드 구현에 따라 경로 수정
    assertFalse(loaded.isEmpty());
    assertEquals("홍길동", loaded.get(0).name);
    System.out.println("saveAndLoadReservationEntry_테스트 완료");
  }

  @Test
  void loadActiveReservationsWithNoFile_테스트() {
    User dummyUser = new User("S123", "123", "정찬", "응소", "학생");
    DummyProfessorViewTest dummyView = new DummyProfessorViewTest(dummyUser);
    ProfessorReserveController controller = new ProfessorReserveController(dummyView, dummyUser);

    String notExist = "no_such_file.json";
    List<ProfessorReserveController.ReservationEntry> loaded = controller.loadActiveReservations(notExist);
    assertTrue(loaded.isEmpty());
    System.out.println("loadActiveReservationsWithNoFile_테스트 완료");
  }
}