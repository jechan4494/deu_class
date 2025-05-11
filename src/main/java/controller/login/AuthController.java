package controller.login;

import model.login.User;
import model.login.UserDAO;

import javax.swing.*;

public class AuthController {
  public static boolean checkDuplicateId(String id) {
    return UserDAO.findUserById(id) != null;
  }

  public static boolean registerUser(User newUser) {
    if (checkDuplicateId(newUser.getId())) {
      JOptionPane.showMessageDialog(null, "중복된 아이디가 있습니다.");
      return false;
    }
    UserDAO.saveUser(newUser);
    JOptionPane.showMessageDialog(null, "회원가입이 완료되었습니다!");
    return true;
  }

  public static User login(String id, String password) {
    User user = UserDAO.findUserById(id);
    if (user != null && user.getPassword().equals(password)) {
      return user;
    }
    return null;
  }
}
