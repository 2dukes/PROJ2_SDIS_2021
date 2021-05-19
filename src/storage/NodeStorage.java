package storage;

import chord.Node;
import chord.NodeInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;

public class NodeStorage {
    private final NodeInfo nodeInfo; // Only for serializing
    private final ConcurrentHashMap<BigInteger, PeerFileStored> filesStored; // Stored by him from other peers
    private final ConcurrentHashMap<BigInteger, PeerFileBackedUp> filesBackedUp; // That he demanded other peers to store

    public NodeStorage(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
        this.filesStored = new ConcurrentHashMap<>();
        this.filesBackedUp = new ConcurrentHashMap<>();
    }

    public void createStoredFile(PeerFileStored peerFile) {
        try {
            String filePath = "../../resources/peers/" + Node.nodeInfo.getId() + "/stored/" + peerFile.fileName;

            File f = new File(filePath);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }

            System.out.println("Storing file...");
            FileOutputStream file = new FileOutputStream(filePath);
            file.write(peerFile.data);

            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addStoredFile(PeerFileStored file) {
        try {
            if (file.getFileId() == null)
                throw new Exception("File ID was not computed yet!");
            this.filesStored.put(file.getFileId(), file);

            createStoredFile(file);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void addFileBackedUp(PeerFileBackedUp file) {
        try {
            if (file.getFileId() == null)
                throw new Exception("File ID was not computed yet!");
            this.filesBackedUp.put(file.getFileId(), file);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public PeerFileStored getStoredFile(BigInteger fileId) {
        return this.filesStored.get(fileId);
    }

    public PeerFileBackedUp getFileBackedUp(BigInteger fileId) {
        return this.filesBackedUp.get(fileId);
    }

    public NodeInfo getNodeInfo() {
        return this.nodeInfo;
    }

    public void removeStoredFile(BigInteger fileId) {
        this.filesStored.remove(fileId);
    }

    public void removeFileBackedUp(BigInteger fileId) {
        this.filesBackedUp.remove(fileId);
    }
}
