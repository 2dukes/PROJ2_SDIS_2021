package messages.SendMessages;

import Threads.ThreadPool;
import chord.Node;
import chord.NodeInfo;
import dispatchers.Sender;
import storage.PeerFile;
import storage.PeerFileBackedUp;

import java.io.IOException;
import java.util.Arrays;

// IP_ORIG PORT_ORIG ID_ORIG FILE FILE_ID FILE_NAME REPLICATION_DEG REPLICATION_NUMBER\n CONTENT
public class SendFile {
    public SendFile(PeerFileBackedUp peerFile, int replicationNumber) throws IOException {
        if(peerFile.getFileId().compareTo(Node.nodeInfo.getId()) != 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(Node.nodeInfo.getAddress().getHostAddress()).append(" ");
            builder.append(Node.nodeInfo.getPort()).append(" ");
            builder.append(Node.nodeInfo.getId());
            builder.append(" FILE ");
            builder.append(peerFile.getFileId()).append(" ");
            builder.append(peerFile.getName()).append(" ");
            builder.append(peerFile.getReplicationDeg()).append(" ");
            builder.append(replicationNumber).append("\n");
            builder.append(Arrays.toString(peerFile.getData()));

            ThreadPool.getInstance().execute(new Sender(Node.successor.getAddress(), Node.successor.getPort(), builder.toString()));
        } else {
            System.err.println("Cannot send a message to myself");
        }

    }
}
