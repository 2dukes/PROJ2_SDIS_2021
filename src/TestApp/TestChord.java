package TestApp;

import Threads.ThreadPool;
import chord.Node;
import messages.Sender;

import java.io.IOException;

public class TestChord {
    public static void main(String[] args) throws IOException {
        System.setProperty("javax.net.ssl.trustStore","../../keys/truststore"); // CLIENT TRUST STORE
        System.setProperty("javax.net.ssl.trustStoreType","JKS");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        System.setProperty("javax.net.ssl.keyStore","../../keys/server.keys");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");

        if(args[0].equals("CLIENT")) {
            ThreadPool.getInstance().execute(new Sender("127.0.0.1", 8000));
        } else if(args[0].equals("SERVER")) {
            Node node = new Node();
        }

        System.out.println("TestApp");
    }
}
