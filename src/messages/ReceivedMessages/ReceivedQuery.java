package messages.ReceivedMessages;

import chord.Node;
import chord.NodeInfo;
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
            List<NodeInfo> visitedInfos = new ArrayList<>();

            BigInteger previousElement = Node.nodeInfo.getId();
            for (BigInteger j : Node.fingerTable.getKeysOrder()) {
                BigInteger i = Node.fingerTable.getNodeInfo(j).getId();
                if(i.compareTo(Node.nodeInfo.getId()) > 0) {
                    leftSideInterval = Node.nodeInfo.getId();
                    rightSideInterval = i;

                    if(this.lookupId.compareTo(leftSideInterval) >= 0 && this.lookupId.compareTo(rightSideInterval) <= 0) {
                        try {
                            new SendQueryResponse(Node.fingerTable.getFingerTable().get(j),
                                    answerNodeInfo,
                                    this.lookupId);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                } else {
                    leftSideInterval = previousElement;
                    rightSideInterval = i;
                    if(this.lookupId.compareTo(leftSideInterval) >= 0 || this.lookupId.compareTo(rightSideInterval) <= 0) {
                        try {
                            new SendQueryResponse(Node.fingerTable.getFingerTable().get(j),
                                    answerNodeInfo,
                                    this.lookupId);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
                previousElement = i;
                visitedInfos.add(Node.fingerTable.getFingerTable().get(i));
            }

            try {
                new SendQuery(answerNodeInfo,
                        visitedInfos.get(visitedInfos.size() - 1),
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
