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
        BigInteger maxId = Node.fingerTable.getMaxId();

        if (maxId.compareTo(this.toAddNodeInfo.getId()) < 0) {
            // node a adicionar está além do maxID deste node
            // IP_ORIG PORT_ORIG ID_ORIG ADD_NODE IP_TOADD PORT_TOADD ID_TOADD

            try {
                new SendAddNode(Node.nodeInfo, this.toAddNodeInfo, Node.fingerTable.getFingerTable().get(maxId));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        List<NodeInfo> visitedInfos = new ArrayList<>();
        for (int i = 0; i < Macros.numberOfBits; i++) {
            BigInteger newCurrentId = Node.nodeInfo.getId().add(new BigInteger(String.valueOf((int) Math.pow(2, i)))); // TODO: check if conversion from double to BigInteger is correct

            if (this.toAddNodeInfo.getId().compareTo(newCurrentId) == 0 && Node.fingerTable.getFingerTable().get(newCurrentId).getId().compareTo(newCurrentId) == 0) {
                System.err.println("Cannot create a node with ID = " + newCurrentId + ", because a node with that ID already exists.");
                return;
            }

            if (this.toAddNodeInfo.getId().compareTo(newCurrentId) <= 0) {
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

        System.err.println("Something bad happened (this should not happen)");
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