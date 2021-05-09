package messages;

import jsse.JSSEConnection;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Listener implements Runnable {
    JSSEConnection connection;

    public Listener() throws IOException {
        this.connection = new JSSEConnection();
    }

    @Override
    public void run() {
        while(true) {
            try {
                SSLSocket clientSocket = this.connection.acceptConnection();

                // PrintWriter outBuf = new PrintWriter(clientSocket.getOutputStream(), true);

                BufferedReader inBuf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String received = inBuf.readLine();
                System.out.format("\nReceived: %s\n", received);

                // Handle message...

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
