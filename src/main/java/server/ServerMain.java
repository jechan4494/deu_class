package server;

import java.net.ServerSocket;
import java.net.Socket;

// studentMain 클래스 import (클래스가 같은 프로젝트에 있어야 함)

public class ServerMain {
  public static void main(String[] args) {
    final int PORT = 12345; // 클라이언트와 맞춰야 함
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      System.out.println("서버가 " + PORT + " 포트에서 시작되었습니다.");

      // 서버가 시작되면 studentMain의 main 메소드 호출
      javax.swing.SwingUtilities.invokeLater(() -> {
        studentMain.main(new String[0]);
      });

      while (true) {
        Socket clientSocket = serverSocket.accept(); // 클라이언트 접속 대기
        System.out.println("새 클라이언트 접속: " + clientSocket.getInetAddress());
        // 각각의 클라이언트 연결을 별도의 스레드로 처리
        new Thread(new ClientHandler(clientSocket)).start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}