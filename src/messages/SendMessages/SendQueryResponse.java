package messages.SendMessages;

import Threads.ThreadPool;
import chord.NodeInfo;
import dispatchers.Sender;

import java.io.IOException;
import java.math.BigInteger;

// IP_RESPONSE PORT_RESPONSE ID_RESPONSE QUERY_RESPONSE LOOKUP_ID LOOKEDUP_ID -> Response
public class SendQueryResponse {
    public SendQueryResponse(NodeInfo currentNodeInfo, NodeInfo contactingNodeInfo, BigInteger lookupId) throws IOException {
        if(lookupId.compareTo(contactingNodeInfo.getId()) != 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(currentNodeInfo.getAddress().getHostAddress()).append(" ");
            builder.append(currentNodeInfo.getPort()).append(" ");
            builder.append(currentNodeInfo.getId()).append(" ");
            builder.append("QUERY_RESPONSE ");
            builder.append(lookupId).append("\n");

            ThreadPool.getInstance().execute(new Sender(contactingNodeInfo, builder.toString()));
        } else {
            System.err.println("Cannot send a message to myself (QUERY_RESPONSE)");
        }
    }
}
