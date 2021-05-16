package chord;

import messages.SendMessages.SendAddNodeSetPredecessor;
import messages.SendMessages.SendAddNodeSetSuccessor;
import messages.SendMessages.SendAddNode;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class AddNode implements Runnable {
    NodeInfo toAddNodeInfo;

    public AddNode(NodeInfo toAddNodeInfo) {
        this.toAddNodeInfo = toAddNodeInfo;
    }

    public void insertNodeBetweenNandSuccessor() throws IOException {
        // Send message to the node that is being added, for setting the successor and predecessor
        new SendAddNodeSetSuccessor(Node.nodeInfo, Node.successor, this.toAddNodeInfo);
        new SendAddNodeSetPredecessor(Node.nodeInfo, Node.nodeInfo, this.toAddNodeInfo);

        // Set predecessor of the next node
        new SendAddNodeSetPredecessor(Node.nodeInfo, this.toAddNodeInfo, Node.successor);
    }

    @Override
    public void run() {
        BigInteger lookUpId = toAddNodeInfo.getId();

        if (lookUpId.compareTo(Node.successor.getId()) == 0 || lookUpId.compareTo(Node.nodeInfo.getId()) == 0) { // Node a inserir já existe
            System.err.println("Cannot create a node with ID = " + lookUpId + ", because a node with that ID already exists.");
            return;
        }

        try {
            if (Node.successor.getId().compareTo(Node.nodeInfo.getId()) > 0) { // Sucessor não deu a volta
                if (lookUpId.compareTo(Node.nodeInfo.getId()) > 0 && lookUpId.compareTo(Node.successor.getId()) < 0) { // Node a inserir está entre Node atual e sucessor(sem voltas)
                    this.insertNodeBetweenNandSuccessor();
                    return;
                }
            } else { // sucessor deu a volta
                if (lookUpId.compareTo(Node.nodeInfo.getId()) > 0) { // LookUpId não deu a volta
                    if (lookUpId.compareTo(Node.nodeInfo.getId()) > 0 && lookUpId.compareTo(Node.successor.getId()) > 0) {
                        this.insertNodeBetweenNandSuccessor();
                        return;
                    }
                } else { // LookUpId deu a volta
                    if (lookUpId.compareTo(Node.nodeInfo.getId()) < 0 && lookUpId.compareTo(Node.successor.getId()) < 0) {
                        this.insertNodeBetweenNandSuccessor();
                        return;
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        // lookUpId não pertence ao intervalo (n, successor]

        try {
            NodeInfo previousNodeInfo = getClosestPrecedingNode(lookUpId);
            if (previousNodeInfo != null && previousNodeInfo.equals(Node.nodeInfo)) {
                System.err.println("Cannot send ADD_NODE to itself!");
                return;
            }
            if (previousNodeInfo == null)
                previousNodeInfo = BuildFingerTable.getImmediatePredecessor(Node.fingerTable.getLastKey());

            new SendAddNode(Node.nodeInfo, this.toAddNodeInfo, previousNodeInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        lookUpId.compareTo(rightSideInterval) < 0) {
                    return nextElementNodeInfo;
                }
            } else { // Deu a volta
                leftSideInterval = nextElement;
                rightSideInterval = fingerTableId;

                if (nextElement.compareTo(Node.nodeInfo.getId()) > 0) { // nextElement não deu a volta
                    if (lookUpId.compareTo(leftSideInterval) > 0 ||
                            lookUpId.compareTo(rightSideInterval) < 0) {
                        return nextElementNodeInfo;
                    }
                } else { // nextElement deu a volta
                    if (lookUpId.compareTo(leftSideInterval) > 0 &&
                            lookUpId.compareTo(rightSideInterval) < 0) {
                        return nextElementNodeInfo;
                    }
                }
            }
        }

        return null;
    }
}
