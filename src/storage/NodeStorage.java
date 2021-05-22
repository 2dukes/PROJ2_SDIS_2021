package storage;

import chord.Node;
import chord.NodeInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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
            String[] splitName = peerFile.getName().split("\\.");
            String extension = splitName[splitName.length - 1];
            peerFile.setPath("../../resources/peers/" + Node.nodeInfo.getId() + "/stored/" + peerFile.getFileId() + "." + extension);

            File f = new File(peerFile.getPath());
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }

            System.out.println("Storing file...");
            FileOutputStream file = new FileOutputStream(peerFile.getPath());
            file.write(peerFile.data);

            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createRestoredFile(String fileName, byte[] fileData) {
        try {

            String path = "../../resources/peers/" + Node.nodeInfo.getId() + "/restored/" + fileName;

            File f = new File(path);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }

            System.out.println("Creating the restored file...");
            FileOutputStream file = new FileOutputStream(path);
            file.write(fileData);

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

    public List<BigInteger> getFilesStoredIds() { return new ArrayList<>(this.filesStored.keySet()); }

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
        File f = new File(this.getStoredFile(fileId).getPath());
        if (f.delete())
            System.out.println("Deleted file with ID: " + fileId);
        else
            System.err.println("Failed to delete file with ID: " + fileId);

        this.filesStored.remove(fileId);
    }

    public void removeFileBackedUp(BigInteger fileId) {
        this.filesBackedUp.remove(fileId);
    }

    public PeerFileBackedUp getFileByPath(String path) {
        for (BigInteger fileId: this.filesBackedUp.keySet()) {
            PeerFileBackedUp file = this.getFileBackedUp(fileId);
            if (file.getPath().equals(path))
                return file;
        }
        return null;
    }
}
