package test.model;

import model.user.User;
import model.user.UserDAO;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class gitUserDaoModelTest {

  @BeforeEach
  void resetUsers() {
    UserDAO.reset();
  }

  @Test
  void testSaveAndGetAllUsers() {
    User user1 = new User("id1", "pw1", "홍길동", "컴퓨터", "STUDENT");
    User user2 = new User("id2", "pw2", "김교수", "전자", "PROFESSOR");

    UserDAO.saveUser(user1);
    UserDAO.saveUser(user2);

    List<User> users = UserDAO.getAllUsers();
    assertEquals(2, users.size());
    assertEquals("id1", users.get(0).getId());
    assertEquals("id2", users.get(1).getId());
  }

  @Test
  void testReset() {
    User user = new User("id3", "pw3", "이학생", "기계", "STUDENT");
    UserDAO.saveUser(user);
    UserDAO.reset();
    List<User> users = UserDAO.getAllUsers();
    assertEquals(0, users.size());
  }
}
