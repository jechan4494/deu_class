package model.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserDAO {
  private static final String FILE_PATH = "users.json";

  // 모든 사용자 가져오기
  public static List<User> getAllUsers() {
    synchronized (UserDAO.class) { // 동시 접근 문제 해결
      try (Reader reader = new FileReader(FILE_PATH)) {
        List<User> users = new Gson().fromJson(reader, new TypeToken<List<User>>() {}.getType());
        return users == null ? new ArrayList<>() : users;
      } catch (FileNotFoundException e) {
        // 파일이 없으면 새로 생성된 유저 리스트 반환
        return new ArrayList<>();
      } catch (IOException e) {
        e.printStackTrace();
        return Collections.emptyList(); // 읽기 실패
      }
    }
  }

  // 사용자 저장
  public static boolean saveUser(User user) {
    synchronized (UserDAO.class) { // 동시 접근 문제 해결
      List<User> users = getAllUsers();
      users.add(user);

      try (Writer writer = new FileWriter(FILE_PATH)) {
        new Gson().toJson(users, writer);
        return true; // 저장 성공
      } catch (IOException e) {
        e.printStackTrace(); // 저장 실패
        return false;
      }
    }
  }

  // 아이디로 사용자 찾기
  public static User findUserById(String id) {
    return getAllUsers().stream()
            .filter(user -> user.getId().equals(id))
            .findFirst()
            .orElse(null);
  }

  // 아이디 사용 가능 여부 확인
  public static boolean isIdAvailable(String id) {
    return findUserById(id) == null; // 중복 없으면 true
  }
}