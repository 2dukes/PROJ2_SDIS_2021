package messages.ReceivedMessages;

import chord.BuildFingerTable;
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
        NodeInfo answerNodeInfo = new NodeInfo(this.IP, this.port, this.ID);

        try {
            if (this.lookupId.compareTo(Node.successor.getId()) == 0) {// Node a inserir já existe
                new SendQueryResponse(Node.successor,
                        answerNodeInfo,
                        this.lookupId);
                return;
            }
            else if(this.lookupId.compareTo(Node.nodeInfo.getId()) == 0) {
                new SendQueryResponse(Node.nodeInfo,
                        answerNodeInfo,
                        this.lookupId);
                return;
            }
            // System.out.println("QUERYING: " + this.lookupId);

            if (Node.successor.getId().compareTo(Node.nodeInfo.getId()) > 0) { // Sucessor não deu a volta
                if (this.lookupId.compareTo(Node.nodeInfo.getId()) > 0 && this.lookupId.compareTo(Node.successor.getId()) < 0) { // Node a inserir está entre Node atual e sucessor(sem voltas)
                    new SendQueryResponse(Node.successor,
                            answerNodeInfo,
                            this.lookupId);
                    return;
                }
            } else { // sucessor deu a volta
                if (this.lookupId.compareTo(Node.nodeInfo.getId()) > 0) { // LookUpId não deu a volta
                    if (this.lookupId.compareTo(Node.nodeInfo.getId()) > 0 && this.lookupId.compareTo(Node.successor.getId()) > 0) {
                        new SendQueryResponse(Node.successor,
                                answerNodeInfo,
                                this.lookupId);
                        return;
                    }
                } else { // LookUpId deu a volta
                    if (this.lookupId.compareTo(Node.nodeInfo.getId()) < 0 && this.lookupId.compareTo(Node.successor.getId()) < 0) {
                        new SendQueryResponse(Node.successor,
                                answerNodeInfo,
                                this.lookupId);
                        return;
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        // lookUpId não pertence ao intervalo (n, successor]

        try {
            NodeInfo previousNodeInfo = getClosestPrecedingNode(this.lookupId);
            if (previousNodeInfo != null && previousNodeInfo.equals(Node.nodeInfo)) {
                System.err.println("Cannot send QUERY to myself!");
                return;
            }
            if (previousNodeInfo == null)
                previousNodeInfo = BuildFingerTable.getImmediatePredecessor(Node.fingerTable.getLastKey());

            new SendQuery(answerNodeInfo,
                    previousNodeInfo,
                    this.lookupId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // --

        /*if(this.ID.compareTo(Node.nodeInfo.getId()) != 0) {
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
        }*/
    }

    public NodeInfo getClosestPrecedingNode(BigInteger lookUpId) {
        BigInteger leftSideInterval, rightSideInterval;
        BigInteger nextElement;
        NodeInfo nextElementNodeInfo;
        List<BigInteger> keysOrder = Node.fingerTable.getKeysOrder();

        for(int i = keysOrder.size() - 1; i > 0; i--) {
            nextElementNodeInfo = Node.fingerTable.getNodeInfo(keysOrder.get(i - 1));
            nextElement = nextElementNodeInfo.getId();
            BigInteger fingerTableId = Node.fingerTable.getNodeInfo(keysOrder.get(i)).getId();
            if(fingerTableId.compareTo(Node.nodeInfo.getId()) > 0) { // Não deu a volta
                leftSideInterval = nextElement;
                rightSideInterval = fingerTableId;

                if(lookUpId.compareTo(leftSideInterval) > 0 &&
                        lookUpId.compareTo(rightSideInterval) <= 0) {
                    return nextElementNodeInfo;
                }
            } else { // Deu a volta
                leftSideInterval = nextElement;
                rightSideInterval = fingerTableId;

                if (nextElement.compareTo(Node.nodeInfo.getId()) > 0) { // nextElement não deu a volta
                    if (lookUpId.compareTo(leftSideInterval) > 0 ||
                            lookUpId.compareTo(rightSideInterval) <= 0) {
                        return nextElementNodeInfo;
                    }
                } else { // nextElement deu a volta
                    if (lookUpId.compareTo(leftSideInterval) > 0 &&
                            lookUpId.compareTo(rightSideInterval) <= 0) {
                        return nextElementNodeInfo;
                    }
                }
            }
        }

        return null;
    }

}
