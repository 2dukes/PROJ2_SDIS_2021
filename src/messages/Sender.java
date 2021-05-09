package messages;

import jsse.JSSEClientConnection;

import java.io.IOException;

public class Sender implements Runnable {
    JSSEClientConnection connection;
    String msg;

    public Sender(String IP, int port, String msg) throws IOException {
        this.connection = new JSSEClientConnection(IP, port);
        this.msg = msg;
    }

    @Override
    public void run() {
        this.connection.sendMessage(this.msg);
    }
}
