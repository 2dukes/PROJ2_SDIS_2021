package messages;

import jsse.JSSEClientConnection;

import java.io.IOException;

public class Sender implements Runnable {
    JSSEClientConnection connection;

    public Sender(String IP, int port) throws IOException {
        this.connection = new JSSEClientConnection(IP, port);
    }

    @Override
    public void run() {
        this.connection.sendMessage("Hello World!");
    }
}
