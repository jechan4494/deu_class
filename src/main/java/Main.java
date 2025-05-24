import controller.AuthController;
import network.ServerConnector;
import view.LoginView;
import java.io.*;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. 소켓 및 Object 스트림 생성
            Socket socket = new Socket("localhost", 9877);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // 2. 컨트롤러 생성
            ServerConnector connector = new ServerConnector("localhost", 9876);
            AuthController controller = new AuthController(connector);

            // 3. LoginView 실행
            new LoginView(controller, socket, out, in).setVisible(true);

        } catch (IOException e) {
            System.out.println("서버 연결 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
