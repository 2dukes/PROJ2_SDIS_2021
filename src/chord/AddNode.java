package chord;

import macros.Macros;
import messages.SendMessages.SendAddNodeSetPredecessor;
import messages.SendMessages.SendAddNodeSetSuccessor;
import messages.SendMessages.SendAddNode;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class AddNode implements Runnable {
    NodeInfo toAddNodeInfo;

    public AddNode(NodeInfo toAddNodeInfo) {
        this.toAddNodeInfo = toAddNodeInfo;
    }

    @Override
    public void run() {
        //BigInteger maxId = Node.fingerTable.getMaxId();

        BigInteger leftSideInterval, rightSideInterval;
        BigInteger lookUpId = toAddNodeInfo.getId();
        List<NodeInfo> visitedInfos = new ArrayList<>();
        BigInteger previousElement = Node.nodeInfo.getId();

        if(lookUpId.compareTo(Node.nodeInfo.getId()) == 0) { // TODO: Send message to kill the node
            System.err.println("Cannot create a node with ID = " + lookUpId + ", because a node with that ID already exists.");
            return;
        }

        for (BigInteger i : Node.fingerTable.getKeysOrder()) {
            if(i.compareTo(Node.nodeInfo.getId()) > 0) {
                leftSideInterval = Node.nodeInfo.getId();
                rightSideInterval = i;
                if(lookUpId.compareTo(i) == 0 && Node.fingerTable.getNodeInfo(lookUpId).getId().compareTo(lookUpId) == 0) {
                    System.err.println("Cannot create a node with ID = " + lookUpId + ", because a node with that ID already exists.");
                    return;
                } else if(lookUpId.compareTo(leftSideInterval) >= 0 && lookUpId.compareTo(rightSideInterval) <= 0) {
                    try {
                        // Send message to the node that is being added, for setting the successor and predecessor
                        if(Node.successor.getId().compareTo(i) <= 0 && Node.successor.getId().compareTo(this.toAddNodeInfo.getId()) >= 0) {
                            // Send message to the node that is being added, for setting the successor and predecessor
                            NodeInfo predecessor = getLastVisitedPredecessor(visitedInfos, Node.fingerTable.getNodeInfo(i), this.toAddNodeInfo.getId());
                            new SendAddNodeSetSuccessor(Node.nodeInfo, Node.successor, this.toAddNodeInfo);
                            new SendAddNodeSetPredecessor(Node.nodeInfo, predecessor, this.toAddNodeInfo);

                            // Set predecessor of the next node
                            new SendAddNodeSetPredecessor(Node.nodeInfo, this.toAddNodeInfo, Node.successor);
                        } else {
                            NodeInfo predecessor = getLastVisitedPredecessor(visitedInfos, Node.fingerTable.getNodeInfo(i), this.toAddNodeInfo.getId());
                            new SendAddNodeSetSuccessor(Node.nodeInfo, Node.fingerTable.getNodeInfo(i), this.toAddNodeInfo);
                            new SendAddNodeSetPredecessor(Node.nodeInfo, predecessor, this.toAddNodeInfo);

                            // Set predecessor of the next node
                            new SendAddNodeSetPredecessor(Node.nodeInfo, this.toAddNodeInfo, Node.fingerTable.getNodeInfo(i));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }
            } else {
                leftSideInterval = previousElement;
                rightSideInterval = i;
                if(lookUpId.compareTo(leftSideInterval) >= 0 && lookUpId.compareTo(rightSideInterval) <= 0) {
                    try {
                        if(Node.successor.getId().compareTo(i) <= 0 && Node.successor.getId().compareTo(this.toAddNodeInfo.getId()) >= 0) {
                            // Send message to the node that is being added, for setting the successor and predecessor
                            // NodeInfo predecessor = getLastVisitedPredecessor(visitedInfos, Node.fingerTable.getNodeInfo(i), this.toAddNodeInfo.getId());
                            new SendAddNodeSetSuccessor(Node.nodeInfo, Node.successor, this.toAddNodeInfo);
                            NodeInfo immediatePredecessor = BuildFingerTable.getImmediatePredecessor(i);
                            new SendAddNodeSetPredecessor(Node.nodeInfo, immediatePredecessor, this.toAddNodeInfo);

                            // Set predecessor of the next node
                            new SendAddNodeSetPredecessor(Node.nodeInfo, this.toAddNodeInfo, Node.successor);
                        } else {
                            // Send message to the node that is being added, for setting the successor and predecessor
                            // NodeInfo predecessor = getLastVisitedPredecessor(visitedInfos, Node.fingerTable.getNodeInfo(i));
                            new SendAddNodeSetSuccessor(Node.nodeInfo, Node.fingerTable.getNodeInfo(i), this.toAddNodeInfo);
                            NodeInfo immediatePredecessor = BuildFingerTable.getImmediatePredecessor(i);
                            new SendAddNodeSetPredecessor(Node.nodeInfo, immediatePredecessor, this.toAddNodeInfo);

                            // Set predecessor of the next node
                            new SendAddNodeSetPredecessor(Node.nodeInfo, this.toAddNodeInfo, Node.fingerTable.getNodeInfo(i));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }
            }
            previousElement = i;
            visitedInfos.add(Node.fingerTable.getNodeInfo(i));
        }

        try {
            if(Node.predecessor.equals(Node.nodeInfo) && Node.successor.equals(Node.nodeInfo)) {
                // Send message to the node that is being added, for setting the successor and predecessor
                // NodeInfo predecessor = getLastVisitedPredecessor(visitedInfos, Node.fingerTable.getNodeInfo(i));
                new SendAddNodeSetSuccessor(Node.nodeInfo, Node.nodeInfo, this.toAddNodeInfo);
                new SendAddNodeSetPredecessor(Node.nodeInfo, Node.nodeInfo, this.toAddNodeInfo);

                // Set predecessor of the node
                Node.predecessor = this.toAddNodeInfo;

                return;

            } else if(Node.fingerTable.getLastKey().compareTo(Node.successor.getId()) > 0) { // Deu a volta
                if(this.toAddNodeInfo.getId().compareTo(Node.fingerTable.getLastKey()) > 0 ||
                        this.toAddNodeInfo.getId().compareTo(Node.successor.getId()) < 0) {
                    // Send message to the node that is being added, for setting the successor and predecessor
                    new SendAddNodeSetSuccessor(Node.nodeInfo, Node.successor, this.toAddNodeInfo);
                    new SendAddNodeSetPredecessor(Node.nodeInfo, Node.nodeInfo, this.toAddNodeInfo);

                    return;
                }
            }

            NodeInfo previousNodeInfo = BuildFingerTable.getImmediatePredecessor(Node.fingerTable.getLastKey());
            new SendAddNode(Node.nodeInfo,
                    this.toAddNodeInfo,
                    previousNodeInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NodeInfo getLastVisitedPredecessor(List<NodeInfo> visitedNodes, NodeInfo selectedNodeInfo, BigInteger toAddID) {
        Collections.reverse(visitedNodes);
        for (NodeInfo nodeInfo: visitedNodes) {
            if(!selectedNodeInfo.equals(nodeInfo) && nodeInfo.getId().compareTo(toAddID) < 0)
                return nodeInfo;
        }
        return Node.nodeInfo;
    }
}
