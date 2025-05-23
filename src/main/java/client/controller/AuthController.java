package client.controller;

import client.network.ServerConnector;
import client.model.User;

public class AuthController {
  private final ServerConnector connector;

  public AuthController(ServerConnector connector) {
    this.connector = connector;
  }

  public String register(User user) {
    try {
      Object result = connector.sendRequest("register", user);
      if ("success".equals(result)) return "회원가입 성공!";
      else return "중복된 아이디가 있습니다.";
    } catch (Exception e) {
      return "서버 오류: " + e.getMessage();
    }
  }

  public User login(String id, String pw) {
    try {
      Object result = connector.sendRequest("login", id, pw);
      return (User) result;
    } catch (Exception e) {
      return null;
    }
  }
}
