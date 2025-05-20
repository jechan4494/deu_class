package test.model;
import model.user.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

  @Test
  void testUserConstructorAndGetters() {
    User user = new User("id1", "pw1", "홍길동", "컴퓨터", "STUDENT");
    assertEquals("id1", user.getId());
    assertEquals("pw1", user.getPassword());
    assertEquals("홍길동", user.getName());
    assertEquals("컴퓨터", user.getDepartment());
    assertEquals("STUDENT", user.getRole());
  }
}
