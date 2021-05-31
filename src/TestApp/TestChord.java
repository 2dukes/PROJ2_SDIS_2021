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
        Node node;
        if(args.length == 2)
            node = new Node(args[1]);
        else if(args.length == 1)
            node = new Node();
        else {
            System.out.println("Usage: <ACCESS_POINT> [ID]");
            return;
        }

        String accessPoint = args[0];
        Remote obj = node;
        RMIService stub = (RMIService) UnicastRemoteObject.exportObject(obj, 0);
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind(accessPoint, stub);

        if(Node.nodeInfo.getPort() != Macros.gatePort) // Not Gate
            new SendAddNode(Node.nodeInfo, Node.nodeInfo, new NodeInfo(InetAddress.getLocalHost().getHostAddress(), Macros.gatePort, BigInteger.ZERO));

        System.out.println(Node.nodeInfo.toString());
    }
}
