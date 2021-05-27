package jsse;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static utils.Utils.getAvailablePort;

public class JSSEServerConnection extends JSSEConnection {
    SSLServerSocket serverSocket;
    SSLServerSocketFactory serverSocketFactory;

    public JSSEServerConnection(int port) {
        this.port = port;
        this.initJSSEServerConnection();
    }

    public JSSEServerConnection() throws IOException {
        this.port = getAvailablePort();
        this.initJSSEServerConnection();
    }

    public void initJSSEServerConnection() {
        this.serverSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            this.serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(this.port);
            this.serverSocket.setNeedClientAuth(true);
            String[] cyphers = {
                    "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
                    "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
                    "SSL_RSA_WITH_RC4_128_MD5",
                    "SSL_RSA_WITH_RC4_128_SHA",
                    "SSL_RSA_WITH_NULL_MD5" ,
                    "TLS_RSA_WITH_AES_128_CBC_SHA",
                    "TLS_DH_anon_WITH_AES_128_CBC_SHA"
            };

            this.serverSocket.setEnabledCipherSuites(cyphers);
        } catch(IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public JSSEServerConnection acceptConnection() throws IOException {
        this.socket = (SSLSocket) this.serverSocket.accept();
        this.outBuf = new PrintWriter(this.socket.getOutputStream(), true);
        this.inBuf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return this;
    }

    public void close() throws IOException {
        this.socket.close();
    }
}
