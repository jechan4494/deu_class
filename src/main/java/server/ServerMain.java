package server;

import client.swing.test.MainApp;

import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.*;

public class ServerMain {
  public static void main(String[] args) {
    // 서버 실행 시 로그인(MainApp) UI도 동시에 실행
    SwingUtilities.invokeLater(() -> {
      MainApp.main(new String[0]);
    });

    final int PORT = 12345;
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      System.out.println("서버가 " + PORT + " 포트에서 시작되었습니다.");

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("새 클라이언트 접속: " + clientSocket.getInetAddress());
        new Thread(new ClientHandler(clientSocket)).start();
      }
    } catch (Exception e) {
      System.err.println("서버 시작 실패: " + e.getMessage());
      e.printStackTrace();
    }
  }

  // TODO: 서버 실행 부분에서 클라이언트가 실행이 되는 구조는 틀린 구조임, 즉 단순히 서버만 실행이 되어야함
}