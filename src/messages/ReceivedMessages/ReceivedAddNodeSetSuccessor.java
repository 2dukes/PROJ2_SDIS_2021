package messages.ReceivedMessages;

import chord.Node;
import chord.NodeInfo;

import java.math.BigInteger;

public class ReceivedAddNodeSetSuccessor extends Message {
    String successorIP;
    int successorPort;
    BigInteger successorID;

    public ReceivedAddNodeSetSuccessor(String msg) {
        super(msg);
    }

    public void parseSpecificMessage() {
        this.successorIP = this.splitMsg[0];
        this.successorPort = Integer.parseInt(this.splitMsg[1]);
        this.successorID = new BigInteger(this.splitMsg[2]);
    }

    @Override
    public void run() {
        Node.successor = new NodeInfo(this.successorIP, this.successorPort, this.successorID);
    }
}
