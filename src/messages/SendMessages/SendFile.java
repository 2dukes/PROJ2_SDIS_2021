package messages.SendMessages;

import chord.Node;
import sslengine.SSLServer;
import storage.PeerFile;

// IP_ORIG PORT_ORIG ID_ORIG FILE FILE_ID FILE_NAME REPLICATION_DEG REPLICATION_NUMBER REMAINING_CHUNKS CHUNK_CONTENT
public class SendFile {
    public SendFile(PeerFile peerFile, int replicationNumber, SSLServer connection) throws InterruptedException {
        if(peerFile.getFileId().compareTo(Node.nodeInfo.getId()) != 0) {
            int numberOfChunks = peerFile.getChunks().size();
            int remaining = numberOfChunks;
            for(int i = 0; i < numberOfChunks; i++) {

                StringBuilder builder = new StringBuilder();
                builder.append(Node.nodeInfo.getAddress().getHostAddress()).append(" ");
                builder.append(Node.nodeInfo.getPort()).append(" ");
                builder.append(Node.nodeInfo.getId());
                builder.append(" FILE ");
                builder.append(peerFile.getFileId()).append(" ");
                builder.append(peerFile.getName()).append(" ");
                builder.append(peerFile.getReplicationDeg()).append(" ");
                builder.append(replicationNumber).append(" ");
                builder.append(--remaining).append(" ");
                builder.append(peerFile.getChunks().get(i)).append("\n");

                try {
                    connection.write(builder.toString());
                } catch(Exception e) {
                    System.err.println("Failed to write!");
                }

                Thread.sleep(100);
            }
        } else {
            System.err.println("Cannot send a message to myself (FILE)");
        }

    }
}
