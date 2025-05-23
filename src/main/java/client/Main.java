public class Main {
  public static void main(String[] args) {
    ServerConnector connector = new ServerConnector("113.198.234.96", 9876);
    AuthController controller = new AuthController(connector);
    new LoginView(controller);
  }
}
