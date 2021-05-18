package chord;

import Threads.ThreadPool;
import dispatchers.Listener;
import rmi.RMIService;
import storage.PeerFile;
import utils.Utils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClassLoaderSpi;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Node implements RMIService {
    public static NodeInfo nodeInfo;
    public static FingerTable fingerTable;
    public static NodeInfo successor;
    public static NodeInfo predecessor;
    public static ConcurrentHashMap<BigInteger, PeerFile> files;
/*
    // ACCESS_POINT
    public void main(String[] args) {
        try {
            System.setProperty("javax.net.ssl.trustStore","../../keys/truststore"); // CLIENT TRUST STORE
            System.setProperty("javax.net.ssl.trustStoreType","JKS");
            System.setProperty("javax.net.ssl.trustStorePassword", "123456");
            System.setProperty("javax.net.ssl.keyStore","../../keys/server.keys");
            System.setProperty("javax.net.ssl.keyStorePassword", "123456");

            String accessPoint = args[0];
            Remote obj = (Remote) new Node();
            RMIService stub = (RMIService) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(accessPoint, (Remote) stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public Node() throws IOException, NoSuchAlgorithmException {
        try {
            String IP = InetAddress.getLocalHost().getHostAddress();
            Listener listener = new Listener();
            Node.nodeInfo = new NodeInfo(IP, listener.getPort(), Utils.hashID(IP, listener.getPort()));
            Node.fingerTable = new FingerTable();
            Node.successor = Node.nodeInfo;
            Node.predecessor = Node.nodeInfo;

            ThreadPool.getInstance().execute(listener);
            ThreadPool.getInstance().scheduleAtFixedRate(new BuildFingerTable(), 0, 5000, TimeUnit.MILLISECONDS);
            ThreadPool.getInstance().scheduleAtFixedRate(new Stabilize(), 0, 5000, TimeUnit.MILLISECONDS);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Node(String id) throws IOException, NoSuchAlgorithmException {
        try {
            String IP = InetAddress.getLocalHost().getHostAddress();
            Listener listener = new Listener();
            Node.nodeInfo = new NodeInfo(IP, listener.getPort(), Utils.hashID(IP, listener.getPort()));
            Node.nodeInfo.setId(new BigInteger(id));
            Node.fingerTable = new FingerTable();
            Node.successor = Node.nodeInfo;
            Node.predecessor = Node.nodeInfo;

            ThreadPool.getInstance().execute(listener);
            ThreadPool.getInstance().scheduleAtFixedRate(new BuildFingerTable(), 0, 5000, TimeUnit.MILLISECONDS);
            ThreadPool.getInstance().scheduleAtFixedRate(new Stabilize(), 0, 5000, TimeUnit.MILLISECONDS);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void addToFingerTable(BigInteger id, NodeInfo nodeInfo) {
        Node.fingerTable.addNode(id, nodeInfo);
    }

    public static void removeFromFingerTable(BigInteger id) { Node.fingerTable.removeNode(id); }

    public static PeerFile getPeerFile(BigInteger fileId) { return Node.files.get(fileId); }

    public static void addPeerFile(BigInteger fileId, PeerFile file) { Node.files.put(fileId, file); }

    public boolean checkPredecessorActive() {
        // TODO: ping to the predecessor node periodically and check if it answers

        return false;
    }

    public void backup(String path, int replicationDeg) throws RemoteException {
        System.out.println("BACKING UP...");

        PeerFile peerFile = new PeerFile(path, replicationDeg);

        for (int i = 0; i < replicationDeg; i++) {
            BigInteger fileId = peerFile.computeId(i);
            System.out.print("ID of file " + i + ": ");
            System.out.println(fileId);
            /*String fileContent = new String(peerFile.getData());
            System.out.println(fileContent);*/
        }
    }


}
