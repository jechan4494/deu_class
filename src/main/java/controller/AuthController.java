package controller;

import model.user.User;
import model.user.UserDAO;

public class AuthController {
  // 로그인은 기존과 동일
  public static User login(String id, String password) {
    User user = UserDAO.findUserById(id);
    if (user != null && user.getPassword().equals(password)) {
      return user;
    }
    return null;
  }

  // 회원가입 시 모든 필드 받도록 수정
  public static boolean registerUser(String id, String password, String name, String department, String role) {
    if (UserDAO.findUserById(id) != null) return false;
    UserDAO.saveUser(new User(id, password, name, department, role));
    return true;
  }
}
