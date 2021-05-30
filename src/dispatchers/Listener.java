package dispatchers;

import Threads.ThreadPool;
import messages.ReceivedMessages.*;
import sslengine.SSLServer;

import java.net.InetAddress;

import static utils.Utils.getAvailablePort;

public class Listener implements Runnable {
    SSLServer connection;
    int port;

    public Listener() throws Exception {
        String IP = InetAddress.getLocalHost().getHostAddress();
        this.port = getAvailablePort();
        this.connection = new SSLServer("TLSv1.2", IP, port);
    }

    public Listener(int port) throws Exception {
        String IP = InetAddress.getLocalHost().getHostAddress();
        this.port = port;
        this.connection = new SSLServer("TLSv1.2", IP, port);
    }

    public int getPort() {
        return this.port;
    }

    @Override
    public void run() {
        while(true) {
            try {
                String receivedMessage = this.connection.start();

                if (receivedMessage != null)
                    handleMessage(receivedMessage);
                else {
                    System.err.println("Null message found!");
                    this.connection.stop();
                    break;
                }

            } catch (Exception e) {
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
            case "DELETE_FILE" -> ThreadPool.getInstance().execute(new ReceivedDeleteFile(msg));
            case "ASK_RESTORED_FILE" -> ThreadPool.getInstance().execute(new ReceivedAskRestoredFile(msg));
            case "RESTORED_FILE" -> ThreadPool.getInstance().execute(new ReceivedRestoredFile(msg));
            case "FILE_CONNECTION" -> ThreadPool.getInstance().execute(new ReceivedFileConnection(msg, false));
            case "RESTORED_CONNECTION" -> ThreadPool.getInstance().execute(new ReceivedFileConnection(msg, true));
            default -> System.err.println("Unknown Message Type:" + messageType);
        }
    }
}
