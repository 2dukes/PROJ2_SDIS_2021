package TestApp;

import chord.Node;
import chord.NodeInfo;
import macros.Macros;
import messages.SendMessages.SendAddNode;
import rmi.RMIService;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;

public class TestChord {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        System.setProperty("javax.net.ssl.trustStore","../../keys/truststore"); // CLIENT TRUST STORE
        System.setProperty("javax.net.ssl.trustStoreType","JKS");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        System.setProperty("javax.net.ssl.keyStore","../../keys/server.keys");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");

        // Usage:
        // ACCESS_POINT
        // ACCESS_POINT

        Node node = new Node();

        /*Node node = args.length == 3 ? new Node(args[2]) : new Node();*/

        String accessPoint = args[0];
        Remote obj = node;
        RMIService stub = (RMIService) UnicastRemoteObject.exportObject(obj, 0);
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind(accessPoint, stub);

        if(Node.nodeInfo.getPort() != Macros.gatePort) // Not Gate
            new SendAddNode(Node.nodeInfo, Node.nodeInfo, new NodeInfo(InetAddress.getLocalHost().getHostAddress(), Macros.gatePort, BigInteger.ZERO));

        System.out.println(Node.nodeInfo.toString());
        //System.out.println("IP=" + Node.nodeInfo.getAddress().getHostAddress() +  " PORT=" + Node.nodeInfo.getPort() + " ID=" + Node.nodeInfo.getId());
    }
}
