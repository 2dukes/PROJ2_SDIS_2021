package messages.ReceivedMessages;

import chord.FileRedistribution;
import chord.Node;
import chord.NodeInfo;

import java.math.BigInteger;

public class ReceivedAddNodeSetPredecessor extends Message {
    String predecessorIP;
    int predecessorPort;
    BigInteger predecessorID;

    public ReceivedAddNodeSetPredecessor(String msg) {
        super(msg);
    }

    public void parseSpecificMessage() {
        this.predecessorIP = this.splitMsg[0];
        this.predecessorPort = Integer.parseInt(this.splitMsg[1]);
        this.predecessorID = new BigInteger(this.splitMsg[2]);
    }

    @Override
    public void run() {
        NodeInfo newPredecessor = new NodeInfo(this.predecessorIP, this.predecessorPort, this.predecessorID);
        new FileRedistribution(newPredecessor).run();
        Node.predecessor = newPredecessor;
    }
}
