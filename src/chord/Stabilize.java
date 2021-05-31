package chord;

import Threads.IssueMessage;
import Threads.ThreadPool;
import macros.Macros;
import messages.SendMessages.SendSetPredecessor;

import java.io.IOException;

public class Stabilize implements Runnable {

    public Stabilize() {}

    @Override
    public void run() {
        if(!Node.successor.getId().equals(Node.predecessor.getId()))
            ThreadPool.getInstance().execute(new IssueMessage(Macros.MSGTYPE.SUBSEQUENT_SUCCESSOR));
        else // Small Optimization
            Node.subsequentSuccessor = Node.nodeInfo;

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

        System.out.print("Subsequent successor of node " + Node.nodeInfo.getId() + " => ");
        System.out.println(Node.subsequentSuccessor.getId());
    }
}
