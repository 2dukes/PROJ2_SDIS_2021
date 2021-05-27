package jsse;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class JSSEConnection {
    protected PrintWriter outBuf;
    protected BufferedReader inBuf;
    protected SSLSocket socket;

    protected int port;

    public String receiveMessage() throws IOException { return inBuf.readLine(); }

    public void sendMessage(String msg) { outBuf.println(msg); }

    public SSLSocket getSocket() { return socket; }

    public int getPort() { return port; }
}
