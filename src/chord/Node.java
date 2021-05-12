package chord;

import Threads.ThreadPool;
import macros.Macros;
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
        String IP = InetAddress.getLocalHost().getHostAddress();
        Listener listener = new Listener();
        ThreadPool.getInstance().execute(listener);
        ThreadPool.getInstance().scheduleAtFixedRate(new BuildFingerTable(), 0, 500, TimeUnit.MILLISECONDS);
        ThreadPool.getInstance().scheduleAtFixedRate(new Stabilize(), 0, 500, TimeUnit.MILLISECONDS);
        Node.nodeInfo = new NodeInfo(IP, listener.getPort(), Utils.hashID(nodeInfo));
        Node.fingerTable = new FingerTable();
        Node.successor = nodeInfo;
        Node.predecessor = nodeInfo;
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

    // checks if the node is the one that is being searched
    public boolean isDesiredNode(BigInteger id) {
        return id.compareTo(Node.nodeInfo.getId()) <= 0; // if id is less than or equal to it's own id
    }
}

// replication -> 2ª replication -> dividir o hash por 2 (ou aplicar uma função determinística, sendo o input o número da replicação)
//             -> 3ª replication -> dividir o hash por 3
//             ...
