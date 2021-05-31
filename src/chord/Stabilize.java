package chord;

import Threads.IssueMessage;
import Threads.ThreadPool;
import macros.Macros;
import messages.ReceivedMessages.ReceivedQueryResponse;
import messages.SendMessages.SendQuery;
import messages.SendMessages.SendSetPredecessor;
import sslengine.SSLServer;
import utils.Utils;

import java.io.IOException;

public class Stabilize implements Runnable {

    public Stabilize() {}

    @Override
    public void run() {
        ThreadPool.getInstance().execute(new IssueMessage(Macros.MSGTYPE.SUBSEQUENT_SUCCESSOR));
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
