package messages.SendMessages;

import Threads.ThreadPool;
import chord.NodeInfo;
import dispatchers.Sender;

import java.io.IOException;

// IP_ORIG PORT_ORIG ID_ORIG SET_SUCC IP PORT ID
public class SendSetSuccessor {
    public SendSetSuccessor(NodeInfo currentNodeInfo, NodeInfo newNodeInfo, NodeInfo contactNodeInfo) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(currentNodeInfo.getAddress().getHostAddress()).append(" ");
        builder.append(currentNodeInfo.getPort()).append(" ");
        builder.append(currentNodeInfo.getId()).append(" ");
        builder.append("SET_SUCC ");
        builder.append(newNodeInfo.getAddress().getHostAddress()).append(" ");
        builder.append(newNodeInfo.getPort()).append(" ");
        builder.append(newNodeInfo.getId()).append("\n");

        ThreadPool.getInstance().execute(new Sender(contactNodeInfo.getAddress(), contactNodeInfo.getPort(), builder.toString()));
    }
}
