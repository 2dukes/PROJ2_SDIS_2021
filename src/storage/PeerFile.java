package storage;

import java.io.Serializable;
import java.math.BigInteger;
import java.lang.Cloneable;

public abstract class PeerFile implements Serializable, Cloneable {
    protected BigInteger fileId;
    protected byte[] data;
    protected int replicationDeg;
    protected String fileSignature;
    protected String fileName;

    public BigInteger getFileId() {
        return this.fileId;
    }

    public byte[] getData() {
        return this.data;
    }

    public int getReplicationDeg() {
        return this.replicationDeg;
    }

    public String getName() {
        return this.fileName;
    }

    @Override
    public PeerFile clone() throws CloneNotSupportedException {
        return (PeerFile) super.clone();
    }
}
