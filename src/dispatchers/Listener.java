package dispatchers;

import Threads.ThreadPool;
import jsse.JSSEServerConnection;
import messages.ReceivedMessages.*;

import java.io.IOException;

public class Listener implements Runnable {
    JSSEServerConnection connection;

    public Listener() throws IOException {
        this.connection = new JSSEServerConnection();
    }

    public int getPort() {
        return this.connection.getPort();
    }

    @Override
    public void run() {
        while(true) {
            try {
                this.connection.acceptConnection();
                String receivedMsg = this.connection.receiveMessage();
                System.out.format("\nReceived: %s\n", receivedMsg);

                handleMessage(receivedMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleMessage(String msg) {
        String messageType = msg.trim().split("\\s+")[3];

        switch (messageType) {
            case "QUERY" -> ThreadPool.getInstance().execute(new ReceivedQuery(msg));
            case "QUERY_RESPONSE" -> ThreadPool.getInstance().execute(new ReceivedQueryResponse(msg));
            case "SET_PRED" -> ThreadPool.getInstance().execute(new ReceivedSetPredecessor(msg));
            case "SET_SUCC" -> ThreadPool.getInstance().execute(new ReceivedSetSuccessor(msg));
            case "ADD_NODE" -> ThreadPool.getInstance().execute(new ReceivedAddNode(msg));
            case "ADD_NODE_SET_SUCC" -> ThreadPool.getInstance().execute(new ReceivedAddNodeSetSuccessor(msg));
            case "ADD_NODE_SET_PRED" -> ThreadPool.getInstance().execute(new ReceivedAddNodeSetPredecessor(msg));
            default -> System.err.println("Unknown Message Type:" + messageType);
        }
    }
}
