package messages.SendMessages;

import Threads.ThreadPool;
import chord.NodeInfo;
import dispatchers.Sender;

import java.io.IOException;

// IP_ORIG PORT_ORIG ID_ORIG ADD_NODE_SET_SUCC IP PORT ID
public class SendAddNodeSetSuccessor {
    public SendAddNodeSetSuccessor(NodeInfo currentNodeInfo, NodeInfo successorNodeInfo, NodeInfo contactNodeInfo) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(currentNodeInfo.getAddress().getHostAddress()).append(" ");
        builder.append(currentNodeInfo.getPort()).append(" ");
        builder.append(currentNodeInfo.getId()).append(" ");
        builder.append("ADD_NODE_SET_SUCC ");
        builder.append(successorNodeInfo.getAddress().getHostAddress()).append(" ");
        builder.append(successorNodeInfo.getPort()).append(" ");
        builder.append(successorNodeInfo.getId());

        ThreadPool.getInstance().execute(new Sender(contactNodeInfo.getAddress(), contactNodeInfo.getPort(), builder.toString()));
    }
}
