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
                        NodeInfo predecessor = getLastVisitedPredecessor(visitedInfos, Node.fingerTable.getFingerTable().get(i));
                        new SendAddNodeSetSuccessor(Node.nodeInfo, Node.fingerTable.getFingerTable().get(i), this.toAddNodeInfo);
                        new SendAddNodeSetPredecessor(Node.nodeInfo, predecessor, this.toAddNodeInfo);

                        // Set predecessor of the next node
                        new SendAddNodeSetPredecessor(Node.nodeInfo, this.toAddNodeInfo, Node.fingerTable.getFingerTable().get(i));
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
                        // Send message to the node that is being added, for setting the successor and predecessor
                        NodeInfo predecessor = getLastVisitedPredecessor(visitedInfos, Node.fingerTable.getNodeInfo(i));
                        new SendAddNodeSetSuccessor(Node.nodeInfo, Node.fingerTable.getNodeInfo(i), this.toAddNodeInfo);
                        new SendAddNodeSetPredecessor(Node.nodeInfo, predecessor, this.toAddNodeInfo);

                        // Set predecessor of the next node
                        new SendAddNodeSetPredecessor(Node.nodeInfo, this.toAddNodeInfo, Node.fingerTable.getNodeInfo(i));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }
            }
            visitedInfos.add(Node.fingerTable.getNodeInfo(i));
        }

        try {
            if(Node.predecessor.equals(Node.nodeInfo) && Node.successor.equals(Node.nodeInfo)) {
                try {
                    // Send message to the node that is being added, for setting the successor and predecessor
                    // NodeInfo predecessor = getLastVisitedPredecessor(visitedInfos, Node.fingerTable.getNodeInfo(i));
                    new SendAddNodeSetSuccessor(Node.nodeInfo, Node.nodeInfo, this.toAddNodeInfo);
                    new SendAddNodeSetPredecessor(Node.nodeInfo, Node.nodeInfo, this.toAddNodeInfo);

                    // Set predecessor of the node
                    Node.predecessor = this.toAddNodeInfo;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return;
            }/* else if(Node.predecessor.equals(Node.successor)) {
                if(Node.successor.getId().compareTo(this.toAddNodeInfo.getId()) > 0) {
                    new SendAddNodeSetSuccessor(Node.nodeInfo, Node.successor, this.toAddNodeInfo);
                    Node.successor = this.toAddNodeInfo;
                    new SendAddNodeSetPredecessor(Node.nodeInfo, Node.nodeInfo, this.toAddNodeInfo);
                } else if(Node.successor.getId().compareTo(this.toAddNodeInfo.getId()) < 0) {
                    new SendAddNode(Node.nodeInfo,
                        this.toAddNodeInfo,
                        Node.successor);
                } else {
                    System.err.println("ERROR!");
                }

                return;
            }*/
            new SendAddNode(Node.nodeInfo,
                    this.toAddNodeInfo,
                    Node.fingerTable.getNodeInfo(visitedInfos.get(visitedInfos.size() - 1).getId()));
        } catch (IOException e) {
            e.printStackTrace();
        }


        /*if (maxId.compareTo(this.toAddNodeInfo.getId()) < 0) {
            // node a adicionar está além do maxID deste node
            // IP_ORIG PORT_ORIG ID_ORIG ADD_NODE IP_TOADD PORT_TOADD ID_TOADD
            System.out.println("Max Id " + maxId);
            try {
                new SendAddNode(Node.nodeInfo, this.toAddNodeInfo, Node.fingerTable.getFingerTable().get(maxId));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        System.out.println("Step 2");
        List<NodeInfo> visitedInfos = new ArrayList<>();
        // BigInteger maxNumberOfNodes = new BigInteger(String.valueOf((int) Math.pow(2, Macros.numberOfBits)));
        for (BigInteger newCurrentId : Node.fingerTable.getKeysOrder()) {
        // for (int i = 0; i < Macros.numberOfBits; i++) {
            System.out.println("Step 3." + newCurrentId);
            //BigInteger newCurrentId = Node.nodeInfo.getId().add(new BigInteger(String.valueOf((int) Math.pow(2, i))))
            //        .mod(maxNumberOfNodes);

            if (this.toAddNodeInfo.getId().compareTo(newCurrentId) == 0 && Node.fingerTable.getFingerTable().get(newCurrentId).getId().compareTo(newCurrentId) == 0) {
                System.err.println("Cannot create a node with ID = " + newCurrentId + ", because a node with that ID already exists.");
                return;
            }

            if (this.toAddNodeInfo.getId().compareTo(newCurrentId) <= 0) { // Entrou aqui.
                try {
                    // Send message to the node that is being added, for setting the successor and predecessor
                    NodeInfo predecessor = getLastVisitedPredecessor(visitedInfos, Node.fingerTable.getFingerTable().get(newCurrentId));
                    new SendAddNodeSetSuccessor(Node.nodeInfo, Node.fingerTable.getFingerTable().get(newCurrentId), this.toAddNodeInfo);
                    new SendAddNodeSetPredecessor(Node.nodeInfo, predecessor, this.toAddNodeInfo);

                    // Set predecessor of the next node
                    new SendAddNodeSetPredecessor(Node.nodeInfo, this.toAddNodeInfo, Node.fingerTable.getFingerTable().get(newCurrentId));
                } catch(IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            visitedInfos.add(Node.fingerTable.getFingerTable().get(newCurrentId));
        }

        System.err.println("Something bad happened (this should not happen)");*/
    }

    public NodeInfo getLastVisitedPredecessor(List<NodeInfo> visitedNodes, NodeInfo selectedNodeInfo) {
        Collections.reverse(visitedNodes);
        for (NodeInfo nodeInfo: visitedNodes) {
            if(!selectedNodeInfo.equals(nodeInfo))
                return nodeInfo;
        }
        return Node.nodeInfo;
    }
}