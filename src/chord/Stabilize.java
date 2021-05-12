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
        System.out.print("Successor of node " + Node.nodeInfo.getId() + ": ");
        System.out.println(Node.successor.getId());

        System.out.print("Predecessor of node " + Node.nodeInfo.getId() + ": ");
        System.out.println(Node.predecessor.getId());
    }
}

/*
    REQUEST PREDECESSOR (it's now necessary, because the thread is always running)
    IP_ORIG PORT_ORIG ID_ORIG ASK_PRED -> Request

    // RESPOND WITH PREDECESSOR
    IP_ORIG PORT_ORIG ID_ORIG SET_PRED


 */