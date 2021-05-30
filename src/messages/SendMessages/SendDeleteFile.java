package messages.SendMessages;

import Threads.ThreadPool;
import chord.NodeInfo;
import dispatchers.Sender;

import java.io.IOException;
import java.math.BigInteger;

// IP_ORIG PORT_ORIG ID_ORIG DELETE_FILE FILE_ID\n
public class SendDeleteFile {
    public SendDeleteFile(NodeInfo currentNodeInfo, NodeInfo contactNodeInfo, BigInteger fileId) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(currentNodeInfo.getAddress().getHostAddress()).append(" ");
        builder.append(currentNodeInfo.getPort()).append(" ");
        builder.append(currentNodeInfo.getId()).append(" ");
        builder.append("DELETE_FILE ");
        builder.append(fileId).append("\n");

        ThreadPool.getInstance().execute(new Sender(contactNodeInfo, builder.toString()));
    }
}
