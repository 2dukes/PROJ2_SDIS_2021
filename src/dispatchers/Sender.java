package dispatchers;

import jsse.JSSEClientConnection;

import java.io.IOException;
import java.net.InetAddress;

public class Sender implements Runnable {
    JSSEClientConnection connection;
    String msg;

    public Sender(InetAddress address, int port, String msg) throws IOException {
        this.connection = new JSSEClientConnection(address.getHostAddress(), port);
        this.msg = msg;
    }

    @Override
    public void run() {
        this.connection.sendMessage(this.msg);
    }
}
