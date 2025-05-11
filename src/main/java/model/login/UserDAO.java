package model.login;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
  private static final String FILE_PATH = "users.json";

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
}
