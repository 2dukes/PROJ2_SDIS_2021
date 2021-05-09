package jsse;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.*;

import static utils.Utils.getAvailablePort;

public class JSSEConnection {
    SSLServerSocket serverSocket;
    SSLServerSocketFactory serverSocketFactory;

    public JSSEConnection() throws IOException {
        this.serverSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        int port = getAvailablePort();

        try {
            serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(port);
            serverSocket.setNeedClientAuth(true);
            String[] cyphers = {
                    "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
                    "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
                    "SSL_RSA_WITH_RC4_128_MD5",
                    "SSL_RSA_WITH_RC4_128_SHA",
                    "SSL_RSA_WITH_NULL_MD5" ,
                    "TLS_RSA_WITH_AES_128_CBC_SHA",
                    "TLS_DH_anon_WITH_AES_128_CBC_SHA"
            };

            serverSocket.setEnabledCipherSuites(cyphers);
        } catch(IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public SSLSocket acceptConnection() throws IOException {
        SSLSocket clientSocket = (SSLSocket) this.serverSocket.accept();
        return clientSocket;
    }
}
