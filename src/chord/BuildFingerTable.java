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
                    new SendQuery(Node.nodeInfo, Node.successor, newCurrentId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void printFingerTable() {
        System.out.println("Finger table of node " + Node.nodeInfo.getId() + ": ");
        for (BigInteger id: Node.fingerTable.getKeysOrder()) {
            System.out.println(id + " => " + Node.fingerTable.getFingerTable().get(id).getId());
        }
        System.out.println();
    }
}
