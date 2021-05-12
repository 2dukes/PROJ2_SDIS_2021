package messages.ReceivedMessages;

import Threads.ThreadPool;
import chord.AddNode;
import chord.NodeInfo;

import java.math.BigInteger;

// IP_ORIG PORT_ORIG ID_ORIG ADD_NODE IP_TOADD PORT_TOADD ID_TOADD
public class ReceivedAddNode extends Message {
    String toAddIP;
    int toAddPort;
    BigInteger toAddID;
    public ReceivedAddNode(String msg) {
        super(msg);
    }

    public void parseSpecificMessage() {
        this.toAddIP = this.splitMsg[0];
        this.toAddPort = Integer.parseInt(this.splitMsg[1]);
        this.toAddID = new BigInteger(this.splitMsg[2]);
    }

    @Override
    public void run() {
        ThreadPool.getInstance().execute(new AddNode(new NodeInfo(this.toAddIP, this.toAddPort, this.toAddID)));
    }
}
