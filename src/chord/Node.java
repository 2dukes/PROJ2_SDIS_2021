package chord;

import Threads.ThreadPool;
import messages.Listener;
import utils.Utils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

public class Node {
    private BigInteger id;
    private NodeInfo nodeInfo;
    private Listener listener;

    // ConcurrentHashMap: Key - ID | Value - NodeIdentifier
    private FingerTable fingerTable;

    private ConcurrentHashMap<BigInteger, NodeInfo> successor;
    private ConcurrentHashMap<BigInteger, NodeInfo> predecessor;

    private ConcurrentHashMap<BigInteger, PeerFile> files;

    public Node() throws IOException {
        String IP = InetAddress.getLocalHost().getHostAddress();
        this.listener = new Listener();
        ThreadPool.getInstance().execute(this.listener);
        this.nodeInfo = new NodeInfo(IP, this.listener.getPort());
        this.fingerTable = new FingerTable();

        try {
            this.computeId();
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }
    }

    private void computeId() throws NoSuchAlgorithmException {
        this.id = Utils.hashID(nodeInfo);
    }

    public BigInteger getId() {
        return id;
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

    public void setSuccessor(ConcurrentHashMap<BigInteger, NodeInfo> successor) {
        this.successor = successor;
    }

    public ConcurrentHashMap<BigInteger, NodeInfo> getSuccessor() {
        return this.successor;
    }

    public void setPredecessor(ConcurrentHashMap<BigInteger, NodeInfo> predecessor) {
        this.predecessor = predecessor;
    }

    public ConcurrentHashMap<BigInteger, NodeInfo> getPredecessor() {
        return this.predecessor;
    }

    public PeerFile getPeerFile(BigInteger fileId) {
        return this.files.get(fileId);
    }

    public void addPeerFile(BigInteger fileId, PeerFile file) {
        this.files.put(fileId, file);
    }

    public boolean checkPredecessorActive() {
        // TODO: ping to the predecessor node periodically and check if it answers

        return false;
    }

    // checks if the node is the one that is being searched
    public boolean isDesiredNode(BigInteger id) {
        return id.compareTo(this.id) <= 0; // if id is less than or equal to it's own id
    }

}
