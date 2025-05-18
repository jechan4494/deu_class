package model.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;

public class UserDAO {
  private static final String FILE_PATH = "src/main/resources/users.json";
  private static final Gson gson = new Gson();
  private static final Type USER_LIST_TYPE = new TypeToken<List<User>>(){}.getType();

  // 파일이 비어 있을 때만 테스트 데이터 추가
  static {
    List<User> existingUsers = getAllUsers();
    if (existingUsers.isEmpty()) {
      saveUser(new User("student", "1234", "홍길동", "컴퓨터공학과", "STUDENT"));
      saveUser(new User("prof", "1234", "김교수", "전자공학과", "PROFESSOR"));
    }
  }

  // 기존 코드 유지 (아래는 변경 없음)
  public static List<User> getAllUsers() {
    try (Reader reader = new FileReader(FILE_PATH)) {
      List<User> users = gson.fromJson(reader, USER_LIST_TYPE);
      return users == null ? new ArrayList<>() : users;
    } catch (IOException e) {
      return new ArrayList<>();
    }
  }

  public static void saveUser(User user) {
    List<User> users = getAllUsers();
    users.add(user);
    try (Writer writer = new FileWriter(FILE_PATH)) {
      gson.toJson(users, writer);
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
}
