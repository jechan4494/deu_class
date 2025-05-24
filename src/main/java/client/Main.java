public class Main {
  public static void main(String[] args) {
    ServerConnector connector = new ServerConnector("localhost", 9876);
    AuthController controller = new AuthController(connector);
    new LoginView(controller);
  }
}
