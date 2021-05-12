package messages.SendMessages;

import Threads.ThreadPool;
import chord.Node;
import chord.NodeInfo;
import dispatchers.Sender;

import java.io.IOException;
import java.math.BigInteger;

// IP_ORIG PORT_ORIG ID_ORIG SET_PRED
public class SendSetPredecessor {
    public SendSetPredecessor(NodeInfo currentNodeInfo, NodeInfo successorNodeInfo) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(currentNodeInfo.getAddress().getHostAddress()).append(" ");
        builder.append(currentNodeInfo.getPort()).append(" ");
        builder.append(currentNodeInfo.getId()).append(" ");
        builder.append("SET_PRED");

        ThreadPool.getInstance().execute(new Sender(successorNodeInfo.getAddress(), successorNodeInfo.getPort(), builder.toString()));
    }
}