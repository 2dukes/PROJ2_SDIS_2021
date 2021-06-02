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
            new SendSetPredecessor(Node.nodeInfo, Node.successor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
