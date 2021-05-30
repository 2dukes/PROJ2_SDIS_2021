package messages.ReceivedMessages;

import chord.Node;
import sslengine.SSLClient;
import storage.PeerFileStored;

import java.util.Base64;

public class ReceivedConnection extends Message {

    public ReceivedConnection(String msg) {
        super(msg);
    }

    @Override
    public void parseSpecificMessage() { }


    @Override
    public void run() {
        try {
            System.out.println("CLIENT CREATION");
            System.out.println("IP= " + this.IP);
            System.out.println("PORT= " + this.port);
            Thread.sleep(1500);
            SSLClient connection = new SSLClient("TLSv1.2", this.IP, this.port);
            connection.connect();
            connection.write("PING");
            System.out.println("CONNECTED!");
            StringBuilder fileDataBuilder = new StringBuilder();
            int remainingChunks = 1;
            ReceivedChunk receivedChunk;
            do {

                String receivedMsg = connection.read();
                // System.out.println("_______-RECEIVED: " + receivedMsg);

                receivedChunk = new ReceivedChunk(receivedMsg);
                /*System.out.println("\n\n\nCHUNK DATA:\n\n\n");
                System.out.println("\n" + receivedChunk.chunkData);*/
                remainingChunks = receivedChunk.remainingChunks;
                System.out.println("Remaining: " + remainingChunks);
                fileDataBuilder.append(receivedChunk.chunkData.trim());
            } while(remainingChunks > 0);
            /*System.out.println("\n\n\n\n\n\n\n\n-----------------FILE-----------------------\n\n\n\n");
            System.out.println(fileDataBuilder);*/
            PeerFileStored fileStored = new PeerFileStored(receivedChunk.fileId, receivedChunk.fileName,
                    Base64.getDecoder().decode(fileDataBuilder.toString()), receivedChunk.replicationNumber, receivedChunk.replicationDeg);

            Node.storage.addStoredFile(fileStored);
            connection.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
