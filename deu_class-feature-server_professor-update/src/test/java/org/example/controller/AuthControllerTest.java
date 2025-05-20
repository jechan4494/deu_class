package org.example.controller;

import controller.AuthController;
import model.user.User;
import model.user.UserDAO;
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
    boolean isRegistered = AuthController.registerUser(
        "testid", "testpw", "홍길동", "컴퓨터공학과", "STUDENT"
    );
    assertTrue(isRegistered);

    User loggedIn = AuthController.login("testid", "testpw");
    assertNotNull(loggedIn);
    assertEquals("홍길동", loggedIn.getName());
  }

  @Test
  void testDuplicateId() {
    boolean firstRegister = AuthController.registerUser(
        "dupId", "pw1", "김학생", "전자공학과", "STUDENT"
    );
    assertTrue(firstRegister);

    boolean secondRegister = AuthController.registerUser(
        "dupId", "pw2", "이교수", "컴퓨터공학과", "PROFESSOR"
    );
    assertFalse(secondRegister);
  }

  @Test
  void testLoginFail() {
    AuthController.registerUser(
        "failUser", "correctPw", "이름", "학과", "TA"
    );

    User result = AuthController.login("failUser", "wrongPw");
    assertNull(result);
  }
}
