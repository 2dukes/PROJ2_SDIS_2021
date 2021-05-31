package storage;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public abstract class PeerFile implements Serializable, Cloneable {
    protected BigInteger fileId;
    protected byte[] data;
    protected int replicationDeg;
    protected String fileSignature;
    protected String fileName;
    private List<String> chunks;

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

    public List<String> getChunks() { return this.chunks; }

    public void computeChunks() {
        try {
            this.chunks = new ArrayList<>();
            String encodedString = new String(Base64.getEncoder().encode(this.data));
            int encodedStringSize = encodedString.length();

            int i, chunkSize = 16200;
            for (i = 0; i < encodedStringSize; i += chunkSize) {
                String chunkData;

                if (encodedStringSize - i >= chunkSize) { // if it's not the last chunk
                    chunkData = encodedString.substring(i, i + chunkSize);
                    this.chunks.add(chunkData);
                } else { // last chunk
                    chunkData = encodedString.substring(i, encodedStringSize);
                    this.chunks.add(chunkData);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public PeerFile clone() throws CloneNotSupportedException {
        return (PeerFile) super.clone();
    }
}
