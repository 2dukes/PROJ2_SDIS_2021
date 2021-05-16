package chord;

import Threads.ThreadPool;
import dispatchers.Listener;
import utils.Utils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Node {
    public static NodeInfo nodeInfo;
    public static FingerTable fingerTable;
    public static NodeInfo successor;
    public static NodeInfo predecessor;
    public static ConcurrentHashMap<BigInteger, PeerFile> files;


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
}

// replication -> 2ª replication -> dividir o hash por 2 (ou aplicar uma função determinística, sendo o input o número da replicação)
//             -> 3ª replication -> dividir o hash por 3
//             ...
