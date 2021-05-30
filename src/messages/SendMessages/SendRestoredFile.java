package messages.SendMessages;

import Threads.ThreadPool;
import chord.Node;
import chord.NodeInfo;
import dispatchers.Sender;
import sslengine.SSLServer;
import storage.PeerFileStored;
import utils.Utils;

import java.io.IOException;
import java.util.List;

// IP_ORIG PORT_ORIG ID_ORIG RESTORED_FILE FILE_ID FILE_NAME REPLICATION_DEG REPLICATION_NUMBER CONTENT\n
// IP_ORIG PORT_ORIG ID_ORIG RESTORED_FILE FILE_ID\n
public class SendRestoredFile {
    public SendRestoredFile(NodeInfo currentNodeInfo, NodeInfo contactNodeInfo, PeerFileStored peerFile, String hasFile) throws Exception {
        if (hasFile.equals("TRUE")) {
            // Connection Setup
            int port = Utils.getAvailablePort();
            SSLServer connection = new SSLServer("TLSv1.2", Node.nodeInfo.getAddress().getHostAddress(), port);
            new SendNewRestoredConnection(new NodeInfo(Node.nodeInfo.getAddress().getHostAddress(), port, Node.nodeInfo.getId()), contactNodeInfo);
            System.out.println("WAITING FOR CONNECTION");
            connection.start();
            System.out.println("GOT CONNECTION");

            List<String> chunks = peerFile.getChunks();
            int remaining = chunks.size();
            for (String chunk: chunks) {
                StringBuilder builder = new StringBuilder();
                builder.append(currentNodeInfo.getAddress().getHostAddress()).append(" ");
                builder.append(currentNodeInfo.getPort()).append(" ");
                builder.append(currentNodeInfo.getId());
                builder.append(" RESTORED_FILE ");
                builder.append(peerFile.getFileId()).append(" ");
                builder.append(peerFile.getName()).append(" ");
                builder.append(peerFile.getReplicationDeg()).append(" ");
                builder.append(peerFile.getReplicationNumber()).append(" ");
                builder.append(--remaining).append(" ");
                builder.append(chunk).append("\n");

                try {
                    connection.write(builder.toString());
                } catch(Exception e) {
                    System.err.println("Failed to write!");
                }

                Thread.sleep(75);
            }
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(currentNodeInfo.getAddress().getHostAddress()).append(" ");
            builder.append(currentNodeInfo.getPort()).append(" ");
            builder.append(currentNodeInfo.getId());
            builder.append(" RESTORED_FILE ");
            builder.append(peerFile.getFileId()).append("\n");

            ThreadPool.getInstance().execute(new Sender(contactNodeInfo.getAddress(), contactNodeInfo.getPort(), builder.toString()));
        }


    }
}
