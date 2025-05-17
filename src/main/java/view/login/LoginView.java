package view.login;

import com.google.gson.Gson;
// ProfessorView import 추가
import controller.professor.ProfessorController;
import view.professor.ProfessorView;
import model.user.User;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import view.ta.FeatureFrame;
import model.ta.ReservationModel;
import controller.ta.ReservationController;
import view.student.StudentReservationFrame;

public class LoginView extends JFrame {
  private JTextField tfId;
  private JPasswordField pfPassword;
  private JButton btnLogin, btnSignUp;
  private static final String SERVER_IP = "localhost"; // 서버 IP 주소
  private static final int SERVER_PORT = 12345; // 서버 포트 번호

  public LoginView() {
    setTitle("로그인");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(350, 200);
    setLocationRelativeTo(null);
    setLayout(new GridLayout(4, 2, 10, 10));

    add(new JLabel("아이디:"));
    tfId = new JTextField();
    add(tfId);

    add(new JLabel("비밀번호:"));
    pfPassword = new JPasswordField();
    add(pfPassword);

    btnLogin = new JButton("로그인");
    btnSignUp = new JButton("회원가입");
    add(btnLogin);
    add(btnSignUp);

    // 로그인 버튼 이벤트 리스너 (서버와 통신)
    btnLogin.addActionListener(e -> {
      String id = tfId.getText();
      String password = new String(pfPassword.getPassword());

      // 서버에 로그인 요청을 비동기로 보냄 (UI가 멈추지 않게)
      new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() throws Exception {
          try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
               PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
               BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 1. 서버에 JSON 요청 전송
            String request = String.format(
                "{\"type\":\"login\", \"id\":\"%s\", \"password\":\"%s\"}",
                id, password
            );
            out.println(request);

            // 2. 서버 응답 받기
            String response = in.readLine();
            Gson gson = new Gson();
            ServerResponse serverResponse = gson.fromJson(response, ServerResponse.class);

            // 3. 응답 처리
            SwingUtilities.invokeLater(() -> {
              if (serverResponse.result != null && serverResponse.result.equals("success")) {
                JOptionPane.showMessageDialog(
                    LoginView.this,
                    "로그인 성공! 역할: " + serverResponse.role
                );
                // 역할에 따른 화면 이동
                switch (serverResponse.role) {
                  case "PROFESSOR":
                    ProfessorView profView = new ProfessorView();
                    new ProfessorController(profView, new User(id, password, null, null, "PROFESSOR"));
                    profView.setVisible(true);
                    break;
                  case "TA":
                    FeatureFrame view = new FeatureFrame();
                    ReservationModel model = new ReservationModel();
                    new ReservationController(model, view);
                    view.setVisible(true);
                    break;
                  case "STUDENT":
                    new StudentReservationFrame(serverResponse.name).setVisible(true);
                    break;
                  default:
                    JOptionPane.showMessageDialog(
                        LoginView.this,
                        "지원하지 않는 역할입니다: " + serverResponse.role
                    );
                    return;
                }
                dispose(); // 현재 창 닫기
              } else {
                JOptionPane.showMessageDialog(
                    LoginView.this,
                    "아이디 또는 비밀번호가 올바르지 않습니다."
                );
              }
            });
          } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> {
              JOptionPane.showMessageDialog(
                  LoginView.this,
                  "서버 연결에 실패했습니다."
              );
            });
            ex.printStackTrace();
          }
          return null;
        }
      }.execute();
    });

    // 회원가입 버튼 이벤트 리스너
    btnSignUp.addActionListener(e -> {
      dispose();
      new SignUpView().setVisible(true);
    });
  }

  // 서버 응답을 파싱하기 위한 내부 클래스
  private static class ServerResponse {
    String result;
    String role;
    String name;
  }
}