package dispatchers;

import Threads.ThreadPool;
import jsse.JSSEServerConnection;
import messages.ReceivedMessages.ReceivedQuery;
import messages.ReceivedMessages.ReceivedQueryResponse;

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
            default -> System.err.println("Unknown Message Type:" + messageType);
        }
    }
}
