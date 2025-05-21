package client.swing.test;
import client.view.login.LoginView;

public class studentMain {
  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(() -> {
      new LoginView().setVisible(true);
    });
  }
}
