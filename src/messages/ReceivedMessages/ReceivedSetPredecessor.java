package messages.ReceivedMessages;

import chord.Node;
import chord.NodeInfo;

// IP_ORIG PORT_ORIG ID_ORIG SET_PRED
public class ReceivedSetPredecessor extends Message {

    public ReceivedSetPredecessor(String msg) {
        super(msg);
    }

    @Override
    public void parseSpecificMessage() { }

    @Override
    public void run() {
        // Need to change logic
        if (this.ID.compareTo(Node.nodeInfo.getId()) != 0) {
            NodeInfo newPredecessor = new NodeInfo(this.IP, this.port, this.ID);
            if (!newPredecessor.equals(Node.nodeInfo)) {
                Node.successor = newPredecessor;
            }

        } else {
            System.err.println("Tried to receive message from myself.");
        }
    }
}
