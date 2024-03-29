package messages.ReceivedMessages;

import java.math.BigInteger;

// IP_ORIG PORT_ORIG ID_ORIG FILE FILE_ID FILE_NAME REPLICATION_DEG REPLICATION_NUMBER CONTENT

public class ChunkMessage extends Message {
    BigInteger fileId;
    String fileName;
    int replicationDeg, replicationNumber, remainingChunks;
    String chunkData;

    public ChunkMessage(String msg) {
        super(msg);
    }

    @Override
    public void parseSpecificMessage() {
        this.fileId = new BigInteger(this.splitMsg[0]);
        this.fileName = this.splitMsg[1];
        this.replicationDeg = Integer.parseInt(this.splitMsg[2]);
        this.replicationNumber = Integer.parseInt(this.splitMsg[3]);
        this.remainingChunks = Integer.parseInt(this.splitMsg[4]);
        this.chunkData = this.splitMsg[5];
    }

    @Override
    public void run() { }
}
