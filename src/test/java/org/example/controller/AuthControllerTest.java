package org.example.controller;

import controller.login.AuthController;
import model.user.User;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

  @BeforeEach
  void resetUsersJson() throws Exception {
    Files.write(Paths.get("users.json"), "[]".getBytes());
  }

  @Test
  void testRegisterUserAndLoginSuccess() {
    User user = new User("testid", "testpw", "홍길동", "컴퓨터", "STUDENT");
    assertTrue(AuthController.registerUser(user));
    User loggedIn = AuthController.login("testid", "testpw");
    assertNotNull(loggedIn);
    assertEquals("홍길동", loggedIn.getName());
  }

  @Test
  void testDuplicateId() {
    User user1 = new User("dup", "pw1", "A", "컴공", "STUDENT");
    User user2 = new User("dup", "pw2", "B", "전자", "PROFESSOR");
    assertTrue(AuthController.registerUser(user1));
    assertFalse(AuthController.registerUser(user2)); // 중복 ID로 실패해야 함
  }

  @Test
  void testLoginFail() {
    User user = new User("loginfail", "pw", "이름", "학과", "TA");
    AuthController.registerUser(user);
    User fail = AuthController.login("loginfail", "wrongpw");
    assertNull(fail); // 비밀번호 틀렸으니 로그인 실패
  }
}
