package messages;

import jsse.JSSEServerConnection;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Listener implements Runnable {
    JSSEServerConnection connection;

    public Listener() throws IOException {
        this.connection = new JSSEServerConnection();
    }

    public int getPort() {
        return this.connection.getPort();
    }

    @Override
    public void run() {
        while(true) {
            try {
                this.connection.acceptConnection();
                System.out.format("\nReceived: %s\n", this.connection.receiveMessage());

                // Handle message...

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
