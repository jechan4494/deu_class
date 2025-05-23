import java.io.*;
import java.net.Socket;

public class ServerConnector {
  private final String host;
  private final int port;

  public ServerConnector(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public Object sendRequest(String command, Object... args) throws Exception {
    System.out.println("1. 소켓 생성");
    try (
        Socket socket = new Socket(host, port);
        // ...
    ) {
      System.out.println("2. OOS 생성");
      ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
      System.out.println("3. OIS 생성");
      ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
      System.out.println("4. 명령 전송");
      oos.writeObject(command);
      for (Object arg : args) oos.writeObject(arg);
      oos.flush();
      System.out.println("5. 응답 대기");
      Object response = ois.readObject();
      System.out.println("6. 응답 받음: " + response);
      return response;
    }
  }
}
