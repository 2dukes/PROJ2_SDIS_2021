package messages.ReceivedMessages;

import chord.Node;
import chord.NodeInfo;

import java.math.BigInteger;

// IP_RESPONSE PORT_RESPONSE ID_RESPONSE QUERY_RESPONSE LOOKUP_ID -> Response
public class ReceivedQueryResponse extends Message {
    BigInteger lookupId;

    public ReceivedQueryResponse(String msg) {
        super(msg);
    }

    public void parseSpecificMessage() {
        this.lookupId = new BigInteger(this.splitMsg[0]);
    }

    @Override
    public void run() {
        if(this.lookupId.compareTo(Node.nodeInfo.getId()) != 0) {
            NodeInfo answerNodeInfo = new NodeInfo(this.IP, this.port, this.ID);
            Node.addToFingerTable(this.lookupId, answerNodeInfo);
        } else {
            System.err.println("Tried to receive message from myself.");
        }
    }
}
