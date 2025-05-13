package org.example.controller;

import org.example.model.User;
import org.example.model.UserDAO;
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
    // 회원가입 (5개의 String 인자 전달)
    boolean isRegistered = AuthController.registerUser(
        "testid",
        "testpw",
        "홍길동",
        "컴퓨터공학과",
        "STUDENT"
    );
    assertTrue(isRegistered);

    // 로그인 테스트
    User loggedIn = AuthController.login("testid", "testpw");
    assertNotNull(loggedIn);
    assertEquals("홍길동", loggedIn.getName());
  }

  @Test
  void testDuplicateId() {
    // 첫 번째 회원가입 (성공)
    boolean firstRegister = AuthController.registerUser(
        "dupId",
        "pw1",
        "김학생",
        "전자공학과",
        "STUDENT"
    );
    assertTrue(firstRegister);

    // 같은 아이디로 두 번째 회원가입 (실패해야 함)
    boolean secondRegister = AuthController.registerUser(
        "dupId",
        "pw2",
        "이교수",
        "컴퓨터공학과",
        "PROFESSOR"
    );
    assertFalse(secondRegister);
  }

  @Test
  void testLoginFail() {
    // 회원가입
    AuthController.registerUser(
        "failUser",
        "correctPw",
        "이름",
        "학과",
        "TA"
    );

    // 잘못된 비밀번호로 로그인 시도
    User result = AuthController.login("failUser", "wrongPw");
    assertNull(result);
  }
}
