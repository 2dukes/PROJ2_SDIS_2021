package chord;

import macros.Macros;
import messages.SendMessages.SendQuery;
import messages.SendMessages.SendQueryResponse;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class BuildFingerTable implements Runnable {

    public BuildFingerTable() {}

    @Override
    public void run() {
        this.printFingerTable();

        for (BigInteger newCurrentId : Node.fingerTable.getKeysOrder()) {
            //BigInteger newCurrentId = currentId.add(new BigInteger(String.valueOf((int) Math.pow(2, i))))
            //        .mod(maxNumberOfNodes);
            if(Node.successor.getId().compareTo(Node.nodeInfo.getId()) > 0 &&
                    newCurrentId.compareTo(Node.nodeInfo.getId()) > 0 &&
                    newCurrentId.compareTo(Node.successor.getId()) <= 0) { // Não dá a volta
                        Node.addToFingerTable(newCurrentId, Node.successor);
            } else if(Node.successor.getId().compareTo(Node.nodeInfo.getId()) < 0 &&
                    (newCurrentId.compareTo(Node.nodeInfo.getId()) > 0 ||
                    newCurrentId.compareTo(Node.successor.getId()) <= 0)) { // Dá a volta
                    Node.addToFingerTable(newCurrentId, Node.successor);
            } else { // successor does the same thing to its successor, and so on...
                try {
                    NodeInfo immediatePredecessor = getImmediatePredecessor(newCurrentId);
                    new SendQuery(Node.nodeInfo, immediatePredecessor, newCurrentId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static NodeInfo getImmediatePredecessor(BigInteger newCurrentId) {
        BigInteger maxNumberOfNodes = new BigInteger(String.valueOf((int) Math.pow(2, Macros.numberOfBits)));
        BigInteger lookUpId = newCurrentId;
        do {
            for (BigInteger currentId : Node.fingerTable.getKeysOrder()) {
                NodeInfo currentNodeInfo = Node.fingerTable.getNodeInfo(currentId);
                if(currentNodeInfo.getId().compareTo(lookUpId) == 0)
                    return currentNodeInfo;
            }
            lookUpId = lookUpId.subtract(BigInteger.ONE).mod(maxNumberOfNodes);
        } while (lookUpId.compareTo(newCurrentId) != 0);

        return Node.nodeInfo;
    }

    public void printFingerTable() {
        System.out.println("Finger table of node " + Node.nodeInfo.getId() + ": ");
        for (BigInteger id: Node.fingerTable.getKeysOrder()) {
            System.out.println(id + " => " + Node.fingerTable.getFingerTable().get(id).getId());
        }
        System.out.println();
    }
}
