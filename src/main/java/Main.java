import controller.AuthController;
import network.ServerConnector;
import view.LoginView;

public class Main {
  public static void main(String[] args) {
    ServerConnector connector = new ServerConnector("localhost", 9876);
    AuthController controller = new AuthController(connector);
    new LoginView(controller);
  }
}
