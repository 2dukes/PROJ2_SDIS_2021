package messages.SendMessages;

import Threads.ThreadPool;
import chord.Node;
import chord.NodeInfo;
import dispatchers.Sender;

import java.io.IOException;
import java.math.BigInteger;

// IP_ORIG PORT_ORIG ID_ORIG QUERY LOOKUP_ID -> Request
public class SendQuery {
    public SendQuery(NodeInfo currentNodeInfo, NodeInfo successorNodeInfo, BigInteger lookupId) throws IOException {
        if(lookupId.compareTo(Node.nodeInfo.getId()) != 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(currentNodeInfo.getAddress().getHostAddress()).append(" ");
            builder.append(currentNodeInfo.getPort()).append(" ");
            builder.append(currentNodeInfo.getId()).append(" ");
            builder.append("QUERY ");
            builder.append(lookupId).append("\n");

            ThreadPool.getInstance().execute(new Sender(successorNodeInfo.getAddress(), successorNodeInfo.getPort(), builder.toString()));
        } else {
            System.err.println("Cannot send a message to myself (QUERY)");
        }
    }
}
