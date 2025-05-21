package model.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
  private static final String FILE_PATH = System.getProperty("user.dir") + "/deu_class/users.json";

  static {
    try {
      File file = new File(FILE_PATH);
      if (!file.exists()) {
        file.getParentFile().mkdirs();
        file.createNewFile();
        List<User> initialUsers = new ArrayList<>();
        initialUsers.add(new User("student", "1234", "홍길동", "컴퓨터공학과", "STUDENT"));
        initialUsers.add(new User("prof", "1234", "김교수", "전자공학과", "PROFESSOR"));
        try (Writer writer = new FileWriter(file)) {
          new Gson().toJson(initialUsers, writer);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static List<User> getAllUsers() {
    try (Reader reader = new FileReader(FILE_PATH)) {
      List<User> users = new Gson().fromJson(reader, new TypeToken<List<User>>(){}.getType());
      return users == null ? new ArrayList<>() : users;
    } catch (IOException | JsonSyntaxException e) {
      e.printStackTrace();
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
}
