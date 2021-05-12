package messages.ReceivedMessages;

import Threads.ThreadPool;
import chord.Node;
import chord.NodeInfo;
import messages.SendMessages.SendQuery;
import messages.SendMessages.SendQueryResponse;

import java.io.IOException;
import java.math.BigInteger;

// IP_ORIG PORT_ORIG ID_ORIG QUERY LOOKUP_ID -> Request
public class ReceivedQuery extends Message {
    BigInteger lookupId;

    public ReceivedQuery(String msg) {
        super(msg);
    }

    public void parseSpecificMessage() {
        this.lookupId = new BigInteger(this.splitMsg[0]);
    }

    @Override
    public void run() {
        // List<BigInteger> fingerTableKeys = new ArrayList<BigInteger>(fingerTableMap.keySet());
        // Collections.sort(fingerTableKeys); // TODO: test if it's working without sort
        BigInteger maxId = Node.fingerTable.getMaxId();
        NodeInfo answerNodeInfo = new NodeInfo(this.IP, this.port, this.ID);

        if (maxId.compareTo(this.lookupId) < 0) {
            // Lookup id is not in the current node's finger table
            // Send message to the next node
            try {
                new SendQuery(answerNodeInfo, Node.successor, this.lookupId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < Node.fingerTable.getFingerTable().keySet().size(); i++) {
            if (Node.fingerTable.getFingerTable().get(i).getId().compareTo(this.lookupId) >= 0) {
                // Send response to the node that it's searching
                try {
                    new SendQueryResponse(Node.fingerTable.getFingerTable().get(i), answerNodeInfo, this.ID, this.lookupId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.err.println("Error when looking for ID: " + this.lookupId);
    }
}
