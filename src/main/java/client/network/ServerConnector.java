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
    System.out.println("[클라이언트] 1. 소켓 생성 시도");
    try (
        Socket socket = new Socket(host, port);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
    ) {
      System.out.println("[클라이언트] 2. 소켓/스트림 생성 완료");
      oos.writeObject(command);
      System.out.println("[클라이언트] 3. 명령 전송: " + command);
      for (Object arg : args) {
        oos.writeObject(arg);
        System.out.println("[클라이언트] 4. 파라미터 전송: " + arg);
      }
      oos.flush();
      System.out.println("[클라이언트] 5. 서버 응답 대기");
      Object response = ois.readObject();
      System.out.println("[클라이언트] 6. 서버 응답 수신: " + response);
      return response;
    } catch (Exception e) {
      System.out.println("[클라이언트] 예외 발생: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }
}
