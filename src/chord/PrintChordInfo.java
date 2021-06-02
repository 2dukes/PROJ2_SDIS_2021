package chord;

import macros.Macros;
import messages.SendMessages.SendQuery;

import java.io.IOException;
import java.math.BigInteger;

public class PrintChordInfo implements Runnable {
    public PrintChordInfo() {}

    @Override
    public void run() {
        this.printFingerTable();
    }


    public void printFingerTable() {
        System.out.println("Finger table of node " + Node.nodeInfo.getId() + ": ");
        for (BigInteger id: Node.fingerTable.getKeysOrder()) {
            System.out.println(id + " => " + Node.fingerTable.getFingerTable().get(id).getId());
        }
        System.out.println("\n");
        this.printSuccessorAndPredecessor();
        System.out.println("\n");
    }


    public void printSuccessorAndPredecessor() {
        System.out.print("Successor of node " + Node.nodeInfo.getI+d() + " => ");
        System.out.println(Node.successor.getId());

        System.out.print("Predecessor of node " + Node.nodeInfo.getId() + " => ");
        System.out.println(Node.predecessor.getId());

        System.out.print("Subsequent successor of node " + Node.nodeInfo.getId() + " => ");
        System.out.println(Node.subsequentSuccessor.getId());
    }
}
