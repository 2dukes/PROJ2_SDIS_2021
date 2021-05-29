package dispatchers;

import jsse.JSSEClientConnection;
import sslengine.SSLClient;

import java.io.IOException;
import java.net.InetAddress;

public class Sender implements Runnable {
    SSLClient connection;
    String msg;

    public Sender(InetAddress address, int port, String msg) {
        try {
            this.connection = new SSLClient("TLSv1.2", address.getHostAddress(), port);
            this.msg = msg;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            this.connection.connect();
            this.connection.write(this.msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
