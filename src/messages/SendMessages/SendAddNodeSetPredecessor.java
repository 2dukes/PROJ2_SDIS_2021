package messages.SendMessages;

import Threads.ThreadPool;
import chord.NodeInfo;
import dispatchers.Sender;

// IP_ORIG PORT_ORIG ID_ORIG ADD_NODE_SET_PRED IP PORT ID MAKE_REDISTRIBUTION
public class SendAddNodeSetPredecessor {
    public SendAddNodeSetPredecessor(NodeInfo currentNodeInfo, NodeInfo predecessorNodeInfo, NodeInfo contactNodeInfo, String makeRedistribution) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append(currentNodeInfo.getAddress().getHostAddress()).append(" ");
        builder.append(currentNodeInfo.getPort()).append(" ");
        builder.append(currentNodeInfo.getId()).append(" ");
        builder.append("ADD_NODE_SET_PRED");
        builder.append(" ").append(predecessorNodeInfo.getAddress().getHostAddress()).append(" ");
        builder.append(predecessorNodeInfo.getPort()).append(" ");
        builder.append(predecessorNodeInfo.getId()).append(" ");
        builder.append(makeRedistribution).append("\n");

        ThreadPool.getInstance().execute(new Sender(contactNodeInfo, builder.toString()));
    }
}
