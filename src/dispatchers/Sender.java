package dispatchers;

import chord.Node;
import chord.NodeInfo;
import macros.Macros;
import messages.SendMessages.SendAddNodeSetPredecessor;
import sslengine.SSLClient;

public class Sender implements Runnable {
    SSLClient connection;
    String msg;
    NodeInfo contactNodeInfo;

    public Sender(NodeInfo contactNodeInfo, String msg) {
        try {
            this.contactNodeInfo = contactNodeInfo;
            this.connection = new SSLClient(Macros.cypherSuite, this.contactNodeInfo.getAddress().getHostAddress(), this.contactNodeInfo.getPort());
            this.msg = msg;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            this.connection.connect();
            this.connection.write(this.msg);
        } catch (Exception e) {
            if (Node.successor.equals(this.contactNodeInfo)) {
                try {
                    Node.semaphore.acquire();
                    System.out.println("\n\n\n---------------------- Successor node went down --------------------\n\n\n");
                    Node.successor = Node.subsequentSuccessor;
                    new SendAddNodeSetPredecessor(Node.nodeInfo, Node.nodeInfo, Node.successor);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                Node.semaphore.release();
            }
        }
    }
}
