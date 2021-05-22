package messages.SendMessages;

import Threads.ThreadPool;
import chord.Node;
import chord.NodeInfo;
import dispatchers.Sender;
import storage.PeerFile;
import storage.PeerFileBackedUp;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;

// IP_ORIG PORT_ORIG ID_ORIG FILE FILE_ID FILE_NAME REPLICATION_DEG REPLICATION_NUMBER\n CONTENT
public class SendFile {
    public SendFile(PeerFile peerFile, int replicationNumber, NodeInfo destinationNodeInfo) throws IOException {
        if(peerFile.getFileId().compareTo(Node.nodeInfo.getId()) != 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(Node.nodeInfo.getAddress().getHostAddress()).append(" ");
            builder.append(Node.nodeInfo.getPort()).append(" ");
            builder.append(Node.nodeInfo.getId());
            builder.append(" FILE ");
            builder.append(peerFile.getFileId()).append(" ");
            builder.append(peerFile.getName()).append(" ");
            builder.append(peerFile.getReplicationDeg()).append(" ");
            builder.append(replicationNumber).append(" ");
            builder.append(new String(Base64.getEncoder().encode(peerFile.getData())));

            ThreadPool.getInstance().execute(new Sender(destinationNodeInfo.getAddress(), destinationNodeInfo.getPort(), builder.toString()));
        } else {
            System.err.println("Cannot send a message to myself (FILE)");
        }

    }
}
