package TestApp;

import chord.Node;
import chord.NodeInfo;
import messages.SendMessages.SendAddNode;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

public class TestChord {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        System.setProperty("javax.net.ssl.trustStore","../../keys/truststore"); // CLIENT TRUST STORE
        System.setProperty("javax.net.ssl.trustStoreType","JKS");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        System.setProperty("javax.net.ssl.keyStore","../../keys/server.keys");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");

        if(args[0].equals("ADD_NODE")) {
            // new Node(args[1]);
            if(args.length == 2)
                new Node(args[1]);
            else
                new Node();
            new SendAddNode(Node.nodeInfo, Node.nodeInfo, new NodeInfo(InetAddress.getLocalHost().getHostAddress(), 6969, BigInteger.ZERO));
            // ThreadPool.getInstance().execute(new Sender(InetAddress.getByName("localhost"), 8000, Node.nodeInfo.getId().toString()));
        } else if(args[0].equals("START")) {
            // Start the network with 1 node (gate)
            if(args.length == 2)
                new Node(args[1]);
            else
                new Node();
        }
        System.out.println(Node.nodeInfo.toString());
        //System.out.println("IP=" + Node.nodeInfo.getAddress().getHostAddress() +  " PORT=" + Node.nodeInfo.getPort() + " ID=" + Node.nodeInfo.getId());
        System.out.println("TestApp");
    }
}
