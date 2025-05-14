package server;
import view.login.LoginView;

public class studentMain {
  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(() -> {
      new LoginView().setVisible(true);
    });
  }
}
