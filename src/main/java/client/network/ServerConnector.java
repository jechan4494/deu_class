package client.network;

import java.io.*;
import java.net.Socket;

public class ServerConnector {
  private final String host;
  private final int port;

  public ServerConnector(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public Object sendRequest(String command, Object... args) throws Exception {
    try (
        Socket socket = new Socket(host, port);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
    ) {
      oos.writeObject(command);
      for (Object arg : args) oos.writeObject(arg);
      return ois.readObject();
    }
  }
}
