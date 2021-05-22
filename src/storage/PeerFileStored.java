package storage;

import java.math.BigInteger;

public class PeerFileStored extends PeerFile implements Cloneable { //Files that the Peer backs up for other Peers
    private int replicationNumber;
    private String path;

    public PeerFileStored(BigInteger fileId, String fileName, byte[] data, int replicationNumber, int replicationDeg) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.data = data;
        this.replicationNumber = replicationNumber;
        this.replicationDeg = replicationDeg;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public int getReplicationNumber() {
        return this.replicationNumber;
    }

    @Override
    public PeerFileStored clone() throws CloneNotSupportedException {
        return (PeerFileStored)  super.clone();
    }
}
