package messages.ReceivedMessages;

import Threads.ThreadPool;
import chord.Node;
import chord.NodeInfo;
import macros.Macros;
import messages.SendMessages.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
        if(this.ID.compareTo(Node.nodeInfo.getId()) != 0) {
            NodeInfo answerNodeInfo = new NodeInfo(this.IP, this.port, this.ID);

            BigInteger leftSideInterval, rightSideInterval;
            BigInteger lookUpId = answerNodeInfo.getId();
            List<NodeInfo> visitedInfos = new ArrayList<>();

            for (BigInteger i : Node.fingerTable.getKeysOrder()) {
                if(i.compareTo(Node.nodeInfo.getId()) > 0) {
                    leftSideInterval = Node.nodeInfo.getId();
                    rightSideInterval = i;

                    if(lookUpId.compareTo(leftSideInterval) >= 0 && lookUpId.compareTo(rightSideInterval) <= 0) {
                        try {
                            new SendQueryResponse(Node.fingerTable.getFingerTable().get(i), answerNodeInfo, this.ID, this.lookupId);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                } else {
                    leftSideInterval = BigInteger.ZERO;
                    rightSideInterval = i;
                    if(lookUpId.compareTo(leftSideInterval) >= 0 && lookUpId.compareTo(rightSideInterval) <= 0) {
                        try {
                            new SendQueryResponse(Node.fingerTable.getFingerTable().get(i), answerNodeInfo, this.ID, this.lookupId);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
                visitedInfos.add(Node.fingerTable.getFingerTable().get(i));
            }

            try {
                new SendQuery(answerNodeInfo,
                        Node.fingerTable.getNodeInfo(visitedInfos.get(visitedInfos.size() - 1).getId()),
                        this.lookupId);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.err.println("Error when looking for ID: " + this.lookupId);
        } else {
            System.err.println("Trying to receive QUERY messages from myself.");
        }
    }
}
