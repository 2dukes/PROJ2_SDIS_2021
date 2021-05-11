package chord;

import Threads.ThreadPool;
import macros.Macros;
import dispatchers.Listener;
import messages.SendMessages.SendQuery;
import utils.Utils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

public class Node {
    private NodeInfo nodeInfo;
    private Listener listener;

    private FingerTable fingerTable;

    private NodeInfo successor;
    private NodeInfo predecessor;

    private ConcurrentHashMap<BigInteger, PeerFile> files;

    public Node() throws IOException, NoSuchAlgorithmException {
        String IP = InetAddress.getLocalHost().getHostAddress();
        this.listener = new Listener();
        ThreadPool.getInstance().execute(this.listener);
        this.nodeInfo = new NodeInfo(IP, this.listener.getPort(), Utils.hashID(nodeInfo));
        this.fingerTable = new FingerTable();
    }

    public FingerTable getFingerTable() {
        return this.fingerTable;
    }

    public void addToFingerTable(BigInteger id, NodeInfo nodeInfo) {
        this.fingerTable.addNode(id, nodeInfo);
    }

    public void removeFromFingerTable(BigInteger id) {
        this.fingerTable.removeNode(id);
    }

    public void setSuccessor(NodeInfo successor) {
        this.successor = successor;
    }

    public NodeInfo getSuccessor() {
        return this.successor;
    }

    public void setPredecessor(NodeInfo predecessor) {
        this.predecessor = predecessor;
    }

    public NodeInfo getPredecessor() {
        return this.predecessor;
    }

    public PeerFile getPeerFile(BigInteger fileId) {
        return this.files.get(fileId);
    }

    public NodeInfo getNodeInfo() { return nodeInfo; }

    public void addPeerFile(BigInteger fileId, PeerFile file) {
        this.files.put(fileId, file);
    }

    public boolean checkPredecessorActive() {
        // TODO: ping to the predecessor node periodically and check if it answers

        return false;
    }

    // checks if the node is the one that is being searched
    public boolean isDesiredNode(BigInteger id) {
        return id.compareTo(this.getNodeInfo().getId()) <= 0; // if id is less than or equal to it's own id
    }

    public void buildFingerTable() throws IOException {
        BigInteger currentId = this.nodeInfo.getId();
        for (int i = 0; i < Macros.numberOfBits; i++) {
            BigInteger newCurrentId = currentId.add(new BigInteger(String.valueOf((int) Math.pow(i, 2)))); // TODO: check if conversion from double to BigInteger is correct

            if (newCurrentId.compareTo(this.successor.getId()) <= 0) {
                fingerTable.addNode(this.successor.getId(), this.successor);
            } else { // successor does the same thing to its successor, and so on...
                new SendQuery(this.nodeInfo, this.successor, newCurrentId);
            }
        }
    }
}

// replication -> 2ª replication -> dividir o hash por 2 (ou aplicar uma função determinística, sendo o input o número da replicação)
//             -> 3ª replication -> dividir o hash por 3
//             ...
