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
        //if (this.ID.compareTo(Node.nodeInfo.getId()) != 0) {
        NodeInfo senderNodeInfo = new NodeInfo(this.IP, this.port, this.ID);
        if (!senderNodeInfo.equals(Node.predecessor)) {
            try {
                new messages.SendMessages.SendSetSuccessor(Node.nodeInfo, Node.predecessor, senderNodeInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //} else {
            //System.err.println("Tried to receive message from myself.");
        //}
    }
}
