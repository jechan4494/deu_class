package server.model.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
  private static final String FILE_PATH = "users.json";

  // 파일이 비어 있을 때만 테스트 데이터 추가
  static {
    List<User> existingUsers = getAllUsers();
    if (existingUsers.isEmpty()) {
      saveUser(new User("student", "1234", "홍길동", "컴퓨터공학과", "STUDENT"));
      saveUser(new User("prof", "1234", "김교수", "전자공학과", "PROFESSOR"));
    }
  }

  public static List<User> getAllUsers() {
    try (Reader reader = new FileReader(FILE_PATH)) {
      List<User> users = new Gson().fromJson(reader, new TypeToken<List<User>>(){}.getType());
      return users == null ? new ArrayList<>() : users;
    } catch (IOException e) {
      return new ArrayList<>();
    }
  }

  public static void saveUser(User user) {
    List<User> users = getAllUsers();
    users.add(user);
    try (Writer writer = new FileWriter(FILE_PATH)) {
      new Gson().toJson(users, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static User findUserById(String id) {
    return getAllUsers().stream()
        .filter(user -> user.getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  // ★ 추가: 테스트용 초기화 메서드
  public static void reset() {
    try (Writer writer = new FileWriter(FILE_PATH)) {
      writer.write("[]");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
