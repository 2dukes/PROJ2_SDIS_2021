package messages.ReceivedMessages;

import chord.Node;
import sslengine.SSLClient;
import storage.PeerFileStored;

import java.util.Base64;

public class ReceivedFileConnection extends Message {
    boolean isRestoredFile;

    public ReceivedFileConnection(String msg, boolean isRestoredFile) {
        super(msg);
        this.isRestoredFile = isRestoredFile;
    }

    @Override
    public void parseSpecificMessage() { }


    @Override
    public void run() {
        try {
            Thread.sleep(1500);
            SSLClient connection = new SSLClient("TLSv1.2", this.IP, this.port);
            connection.connect();
            connection.write("PING");
            StringBuilder fileDataBuilder = new StringBuilder();
            int remainingChunks;
            ChunkMessage chunkMessage;
            do {
                String receivedMsg = connection.read();
                chunkMessage = new ChunkMessage(receivedMsg);
                remainingChunks = chunkMessage.remainingChunks;

                System.out.println("Remaining Chunks: " + remainingChunks);
                fileDataBuilder.append(chunkMessage.chunkData.trim());
            } while(remainingChunks > 0);

            if(isRestoredFile) { // When it belongs to the RESTORE PROTOCOL
                Node.storage.createRestoredFile(chunkMessage.fileName, Base64.getDecoder().decode(fileDataBuilder.toString()));
                Node.fileIdsConsultedForRestore.clear();
            } else { // When it belongs to the BACKUP PROTOCOL or FILE REDISTRIBUTION
                PeerFileStored fileStored = new PeerFileStored(chunkMessage.fileId, chunkMessage.fileName,
                        Base64.getDecoder().decode(fileDataBuilder.toString()), chunkMessage.replicationNumber, chunkMessage.replicationDeg);

                Node.storage.addStoredFile(fileStored);
            }

            connection.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
