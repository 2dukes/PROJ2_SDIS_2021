package TestApp;

import Threads.ThreadPool;
import chord.Node;
import dispatchers.Sender;

import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

public class TestChord {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        System.setProperty("javax.net.ssl.trustStore","../../keys/truststore"); // CLIENT TRUST STORE
        System.setProperty("javax.net.ssl.trustStoreType","JKS");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        System.setProperty("javax.net.ssl.keyStore","../../keys/server.keys");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");

        if(args[0].equals("CLIENT")) {
            Node node = new Node();
            ThreadPool.getInstance().execute(new Sender(InetAddress.getByName("localhost"), 8000, Node.nodeInfo.getId().toString()));
        } else if(args[0].equals("SERVER")) {
            Node node = new Node();
        }

        System.out.println("TestApp");
    }
}
