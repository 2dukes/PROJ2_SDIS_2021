package jsse;

import macros.Macros;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;

public class JSSEClientConnection extends JSSEConnection {
    SSLSocketFactory socketFactory;

    public JSSEClientConnection(String IP, int port) throws IOException {
        this.socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try {
            this.socket = (SSLSocket) socketFactory.createSocket(InetAddress.getByName(IP), port);
            String[] cyphers = {
                    "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
                    "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
                    "SSL_RSA_WITH_RC4_128_MD5",
                    "SSL_RSA_WITH_RC4_128_SHA",
                    "SSL_RSA_WITH_NULL_MD5" ,
                    "TLS_RSA_WITH_AES_128_CBC_SHA",
                    "TLS_DH_anon_WITH_AES_128_CBC_SHA"
            };

            this.socket.setEnabledCipherSuites(cyphers);
            this.outBuf = new PrintWriter(this.socket.getOutputStream(), true);
            this.inBuf = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            // this.socket.setSoTimeout(1500);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        this.socket.close();
    }
}
