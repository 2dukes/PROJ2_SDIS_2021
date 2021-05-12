package messages.ReceivedMessages;

import chord.Node;
import chord.NodeInfo;

import java.math.BigInteger;

// IP_ORIG PORT_ORIG ID_ORIG SET_SUCC IP PORT ID
public class ReceivedSetSuccessor extends Message {
    String newSuccessorIP;
    int newSuccessorPort;
    BigInteger newSuccessorID;

    public ReceivedSetSuccessor(String msg) {
        super(msg);
    }

    @Override
    public void parseSpecificMessage() {
        this.newSuccessorIP = this.splitMsg[0];
        this.newSuccessorPort = Integer.parseInt(this.splitMsg[1]);
        this.newSuccessorID = new BigInteger(this.splitMsg[2]);
    }

    @Override
    public void run() {
        if (this.ID.compareTo(Node.nodeInfo.getId()) != 0) {
            Node.successor = new NodeInfo(this.newSuccessorIP, this.newSuccessorPort, this.newSuccessorID);
        } else {
            System.err.println("Tried to receive message from myself.");
        }
    }
}
