package messages.ReceivedMessages;

import chord.Node;
import sslengine.SSLClient;
import storage.PeerFileStored;

import java.util.Base64;

public class ReceivedRestoredConnection extends Message {
    public ReceivedRestoredConnection(String msg) {
        super(msg);
    }

    @Override
    public void parseSpecificMessage() {

    }

    @Override
    public void run() {
        try {
            System.out.println("CLIENT CREATION");
            Thread.sleep(1500);
            SSLClient connection = new SSLClient("TLSv1.2", this.IP, this.port);
            connection.connect();
            connection.write("PING");
            System.out.println("CONNECTED!");
            StringBuilder fileDataBuilder = new StringBuilder();
            int remainingChunks;
            ReceivedChunk receivedChunk;
            do {
                String receivedMsg = connection.read();
                System.out.println("RECEIVED: " + receivedMsg);
                receivedChunk = new ReceivedChunk(receivedMsg);
                remainingChunks = receivedChunk.remainingChunks;

                System.out.println("Remaining: " + remainingChunks);
                fileDataBuilder.append(receivedChunk.chunkData.trim());
            } while(remainingChunks > 0);

            Node.storage.createRestoredFile(receivedChunk.fileName, Base64.getDecoder().decode(fileDataBuilder.toString()));
            Node.fileIdsConsultedForRestore.clear();

            connection.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
