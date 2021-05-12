package chord;

import messages.SendMessages.SendSetPredecessor;

import java.io.IOException;

public class Stabilize implements Runnable {

    public Stabilize() {}

    @Override
    public void run() {
        try {
            new SendSetPredecessor(Node.nodeInfo, Node.successor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*
    REQUEST PREDECESSOR (it's now necessary, because the thread is always running)
    IP_ORIG PORT_ORIG ID_ORIG ASK_PRED -> Request

    // RESPOND WITH PREDECESSOR
    IP_ORIG PORT_ORIG ID_ORIG SET_PRED


 */