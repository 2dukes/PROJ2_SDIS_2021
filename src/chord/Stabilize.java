package chord;

import messages.SendMessages.SendSetPredecessor;

import java.io.IOException;

public class Stabilize implements Runnable {

    public Stabilize() {}

    @Override
    public void run() {
        try {
            this.printSuccessorAndPredecessor();
            new SendSetPredecessor(Node.nodeInfo, Node.successor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printSuccessorAndPredecessor() {
        System.out.print("Successor of node " + Node.nodeInfo.getId() + " => ");
        System.out.println(Node.successor.getId());

        System.out.print("Predecessor of node " + Node.nodeInfo.getId() + " => ");
        System.out.println(Node.predecessor.getId());
    }
}
