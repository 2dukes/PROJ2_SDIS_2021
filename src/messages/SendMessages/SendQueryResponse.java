package messages.SendMessages;

import Threads.ThreadPool;
import chord.Node;
import chord.NodeInfo;
import dispatchers.Sender;

import java.io.IOException;
import java.math.BigInteger;

// IP_RESPONSE PORT_RESPONSE ID_RESPONSE QUERY_RESPONSE LOOKUP_ID LOOKEDUP_ID -> Response
public class SendQueryResponse {
    public SendQueryResponse(NodeInfo currentNodeInfo, NodeInfo contactingNodeInfo, BigInteger lookupId, BigInteger lookedupId) throws IOException {
        if(lookupId.compareTo(Node.nodeInfo.getId()) != 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(currentNodeInfo.getAddress().getHostAddress()).append(" ");
            builder.append(currentNodeInfo.getPort()).append(" ");
            builder.append(currentNodeInfo.getId()).append(" ");
            builder.append("QUERY_RESPONSE ");
            builder.append(lookupId).append(" ");
            builder.append(lookedupId);

            ThreadPool.getInstance().execute(new Sender(contactingNodeInfo.getAddress(), contactingNodeInfo.getPort(), builder.toString()));
        } else {
            System.err.println("Cannot send a message to myself");
        }
    }
}
