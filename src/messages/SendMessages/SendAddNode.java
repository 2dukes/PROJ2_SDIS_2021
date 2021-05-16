package messages.SendMessages;

import Threads.ThreadPool;
import chord.NodeInfo;
import dispatchers.Sender;

import java.io.IOException;

// IP_ORIG PORT_ORIG ID_ORIG ADD_NODE IP_TOADD PORT_TOADD ID_TOADD
public class SendAddNode {
    public SendAddNode(NodeInfo currentNodeInfo, NodeInfo toAddInfo, NodeInfo contactNodeInfo) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(currentNodeInfo.getAddress().getHostAddress()).append(" ");
        builder.append(currentNodeInfo.getPort()).append(" ");
        builder.append(currentNodeInfo.getId()).append(" ");
        builder.append("ADD_NODE ");
        builder.append(toAddInfo.getAddress().getHostAddress()).append(" ");
        builder.append(toAddInfo.getPort()).append(" ");
        builder.append(toAddInfo.getId()).append("\n");

        ThreadPool.getInstance().execute(new Sender(contactNodeInfo.getAddress(), contactNodeInfo.getPort(), builder.toString()));
    }
}
