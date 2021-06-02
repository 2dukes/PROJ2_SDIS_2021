package chord;

import Threads.IssueMessage;
import Threads.ThreadPool;
import dispatchers.Listener;
import macros.Macros;
import rmi.RMIService;
import storage.NodeStorage;
import storage.PeerFileBackedUp;
import storage.PeerFileStored;
import utils.Utils;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class Node implements RMIService {
    public static Listener listener;
    public static NodeInfo nodeInfo;
    public static FingerTable fingerTable;
    public static NodeInfo successor;
    public static NodeInfo subsequentSuccessor;
    public static NodeInfo predecessor;
    public static NodeStorage storage;
    public static List<BigInteger> fileIdsConsultedForRestore;
    public static Semaphore semaphore = new Semaphore(1, true);


    public Node() throws IOException, NoSuchAlgorithmException {
        try {
            this.initNode(false, "");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Node(String id) throws IOException, NoSuchAlgorithmException {
        try {
            this.initNode(true, id);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void initNode(boolean hasId, String id) throws Exception {
        String IP = InetAddress.getLocalHost().getHostAddress();
        if(hasId) {
            if(Node.deserializeStorage(id, IP))
                Node.listener = new Listener(Node.nodeInfo.getPort());
        } else {
            Node.listener = new Listener();
            Node.nodeInfo = new NodeInfo(IP, Node.listener.getPort(), Utils.hashID(IP, Node.listener.getPort()));
            if(!Node.deserializeOnlyStorage(Node.nodeInfo.getId().toString(), IP))
                storage = new NodeStorage(Node.nodeInfo);
        }

        Node.fingerTable = new FingerTable();
        Node.successor = Node.nodeInfo;
        Node.subsequentSuccessor = Node.nodeInfo;
        Node.predecessor = Node.nodeInfo;

        ThreadPool.getInstance().execute(Node.listener);
        ThreadPool.getInstance().scheduleAtFixedRate(new BuildFingerTable(), 0, 750, TimeUnit.MILLISECONDS);
        ThreadPool.getInstance().scheduleAtFixedRate(new Stabilize(), 0, 750, TimeUnit.MILLISECONDS);
        ThreadPool.getInstance().scheduleAtFixedRate(new PrintChordInfo(), 0, 2500, TimeUnit.MILLISECONDS);
        // https://stackoverflow.com/questions/1611931/catching-ctrlc-in-java
        Runtime.getRuntime().addShutdownHook(new Thread(Node::serializeStorage));
    }

    public static void addToFingerTable(BigInteger id, NodeInfo nodeInfo) {
        Node.fingerTable.addNode(id, nodeInfo);
    }

    public static void removeFromFingerTable(BigInteger id) { Node.fingerTable.removeNode(id); }

    public void backup(String path, int replicationDeg) throws RemoteException {
        System.out.println("BACKING UP...");

        try {
            PeerFileBackedUp peerFile = new PeerFileBackedUp(path, replicationDeg);

            peerFile.computeId(0);
            storage.addFileBackedUp(peerFile);
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
            System.out.println("\n\nAn error occurred while processing file.\n\n");
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
                    System.out.println("Deleted file with ID: " + fileId);

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

    public void restore(String path) throws RemoteException {
        System.out.println("RESTORING...");

        Node.fileIdsConsultedForRestore = new ArrayList<>();

        PeerFileBackedUp fileToRestore = storage.getFileByPath(path);

        if (fileToRestore == null) {
            System.err.println("There is no file with path: " + path);
            return;
        }

        int replicationNumber = 0;
        int peerCount = 0;

        try {
            while (peerCount < fileToRestore.getReplicationDeg()) {
                PeerFileBackedUp copyPeerFile = fileToRestore.clone();
                BigInteger fileId = copyPeerFile.computeId(replicationNumber);
                if(fileId.compareTo(nodeInfo.getId()) != 0) {
                    Node.fileIdsConsultedForRestore.add(fileId);
                    peerCount++;
                }
                replicationNumber++;
            }

            PeerFileStored peerFileDummy = new PeerFileStored(
                    Node.fileIdsConsultedForRestore.get(0),
                    "",
                    null,
                    0,
                    0
            );

            System.out.print("Restored file with ID: " + peerFileDummy.getFileId() + "\n");
            ThreadPool.getInstance().execute(new IssueMessage(peerFileDummy, 0, Macros.MSGTYPE.RESTORE));
        } catch (CloneNotSupportedException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void serializeStorage() {
        try {
            System.out.println("\n\nSaving Node's content...");
            String fileName = "../../resources/peers/" + Node.nodeInfo.getId() + "/nodeStorage.ser";

            File f = new File(fileName);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }

            FileOutputStream file = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(storage);

            out.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deserializeOnlyStorage(String id, String IP) {
        try {
            System.out.println("\n\nLoading Node's content...");
            String fileName = "../../resources/peers/" + id + "/nodeStorage.ser";
            File f = new File(fileName);
            if (f.exists()) {
                FileInputStream file = new FileInputStream(fileName);
                ObjectInputStream in = new ObjectInputStream(file);
                storage = (NodeStorage) in.readObject();
                in.close();
                file.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deserializeStorage(String id, String IP) {
        try {
            System.out.println("\n\nLoading Node's content...");
            String fileName = "../../resources/peers/" + id + "/nodeStorage.ser";
            File f = new File(fileName);
            if (!f.exists()) {
                Node.listener = new Listener();
                Node.nodeInfo = new NodeInfo(IP, Node.listener.getPort(), new BigInteger(id));
                storage = new NodeStorage(Node.nodeInfo);
                return false;
            } else {
                FileInputStream file = new FileInputStream(fileName);
                ObjectInputStream in = new ObjectInputStream(file);
                storage = (NodeStorage) in.readObject();
                nodeInfo = storage.getNodeInfo();
                in.close();
                file.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
