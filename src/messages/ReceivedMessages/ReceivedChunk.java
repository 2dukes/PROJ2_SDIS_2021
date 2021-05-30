package messages.ReceivedMessages;

import chord.Node;
import storage.PeerFileStored;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;

// IP_ORIG PORT_ORIG ID_ORIG FILE FILE_ID FILE_NAME REPLICATION_DEG REPLICATION_NUMBER CONTENT
// IP_ORIG PORT_ORIG ID_ORIG RESTORED_FILE FILE_ID FILE_NAME REPLICATION_DEG REPLICATION_NUMBER CONTENT\n

public class ReceivedChunk extends Message {
    BigInteger fileId;
    String fileName;
    int replicationDeg, replicationNumber, remainingChunks;
    String chunkData;

    public ReceivedChunk(String msg) {
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
    public void run() {
        /*System.out.println("Received file! :) ");
        System.out.println("File ID: " + this.fileId);
        System.out.println("File Name: " + this.fileName);
        System.out.println("Replication Degree: " + this.replicationDeg);
        System.out.println("Replication Number: " + this.replicationNumber);
        //System.out.println("Data: " + new String(this.fileData));

        PeerFileStored file = new PeerFileStored(this.fileId, this.fileName, this.fileData, this.replicationNumber, this.replicationDeg);
        Node.storage.addStoredFile(file);*/
    }
}
