package org.example;

import org.example.view.LoginView;

public class Main {
  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(() -> {
      new LoginView().setVisible(true);
    });
  }
}
