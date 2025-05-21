package client.handler;

import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerConnector {
  private static final String SERVER_IP = "localhost";
  private static final int SERVER_PORT = 12345;

  // 로그인 요청
  public static Map<String, String> login(String id, String password) {
    try (
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
    ) {
      Map<String, String> req = new HashMap<>();
      req.put("type", "login");
      req.put("id", id);
      req.put("password", password);

      String reqJson = new Gson().toJson(req);
      out.println(reqJson);

      String respJson = in.readLine();
      return new Gson().fromJson(respJson, Map.class);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  // 회원가입 요청
  public static Map<String, String> signup(String id, String password, String name, String department, String role) {
    try (
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
    ) {
      Map<String, String> req = new HashMap<>();
      req.put("type", "signup");
      req.put("id", id);
      req.put("password", password);
      req.put("name", name);
      req.put("department", department);
      req.put("role", role);

      String reqJson = new Gson().toJson(req);
      out.println(reqJson);

      String respJson = in.readLine();
      return new Gson().fromJson(respJson, Map.class);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
