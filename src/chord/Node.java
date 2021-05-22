package chord;

import Threads.IssueMessage;
import Threads.ThreadPool;
import dispatchers.Listener;
import macros.Macros;
import messages.SendMessages.SendDeleteFile;
import messages.SendMessages.SendFile;
import rmi.RMIService;
import storage.NodeStorage;
import storage.PeerFile;
import storage.PeerFileBackedUp;
import utils.Utils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class Node implements RMIService {
    public static NodeInfo nodeInfo;
    public static FingerTable fingerTable;
    public static NodeInfo successor;
    public static NodeInfo predecessor;
    public static NodeStorage storage;

    public Node() throws IOException, NoSuchAlgorithmException {
        try {
            String IP = InetAddress.getLocalHost().getHostAddress();
            Listener listener = new Listener();
            Node.nodeInfo = new NodeInfo(IP, listener.getPort(), Utils.hashID(IP, listener.getPort()));
            Node.fingerTable = new FingerTable();
            Node.successor = Node.nodeInfo;
            Node.predecessor = Node.nodeInfo;
            storage = new NodeStorage(Node.nodeInfo);

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
            storage = new NodeStorage(Node.nodeInfo);

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

    public boolean checkPredecessorActive() {
        // TODO: ping to the predecessor node periodically and check if it answers

        return false;
    }

    public void backup(String path, int replicationDeg) throws RemoteException {
        System.out.println("BACKING UP...");

        PeerFileBackedUp peerFile = new PeerFileBackedUp(path, replicationDeg);
        peerFile.computeId(0);
        storage.addFileBackedUp(peerFile);

        try {
            int replicationNumber = 0;
            int peerStoredCount = 0;
            while (peerStoredCount < replicationDeg) {
                PeerFileBackedUp copyPeerFile = peerFile.clone();
                BigInteger fileId = copyPeerFile.computeId(replicationNumber);
                if(fileId.compareTo(nodeInfo.getId()) != 0) {
                    System.out.print("ID of file " + replicationNumber + ": ");
                    System.out.println(fileId);

                    ThreadPool.getInstance().execute(new IssueMessage(copyPeerFile, replicationNumber, Macros.MSGTYPE.BACKUP));
                    peerStoredCount++;
                }
                else {
                    System.err.println("Cannot upload a file to itself");
                }
                replicationNumber++;
            }
        } catch (CloneNotSupportedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(String path) throws RemoteException {
        System.out.println("DELETING...");

        PeerFileBackedUp fileToDelete = storage.getFileByPath(path);

        if (fileToDelete == null) {
            System.err.println("There is no file with path: " + path);
            return;
        }

        int replicationNumber = 0;
        int peerCount = 0;
        try {
            while (peerCount < fileToDelete.getReplicationDeg()) {
                PeerFileBackedUp copyPeerFile = fileToDelete.clone();
                BigInteger fileId = copyPeerFile.computeId(replicationNumber);
                if(fileId.compareTo(nodeInfo.getId()) != 0) {
                    System.out.print("Deleted file with ID: " + fileId);

                    ThreadPool.getInstance().execute(new IssueMessage(copyPeerFile, replicationNumber, Macros.MSGTYPE.DELETE));
                    peerCount++;
                }

                replicationNumber++;
            }
            storage.removeFileBackedUp(fileToDelete.getFileId());
        } catch (CloneNotSupportedException | IOException e) {
            e.printStackTrace();
        }
    }

}
