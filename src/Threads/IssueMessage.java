package Threads;

import chord.Node;
import chord.NodeInfo;
import jdk.swing.interop.SwingInterOpUtils;
import jsse.JSSEServerConnection;
import macros.Macros;
import messages.ReceivedMessages.ReceivedQueryResponse;
import messages.SendMessages.*;
import sslengine.SSLServer;
import storage.PeerFile;
import utils.Utils;

import java.io.IOException;

public class IssueMessage implements Runnable {
    SSLServer connection;
    NodeInfo originalInfo;
    PeerFile file;
    int replicationNumber;
    Macros.MSGTYPE msgType;

    public IssueMessage(PeerFile file, int replicationNumber, Macros.MSGTYPE msgType) throws IOException {
        try {
            this.connection = new SSLServer("TLSv1.2", Node.nodeInfo.getAddress().getHostAddress(), Utils.getAvailablePort());
            this.file = file;
            this.replicationNumber = replicationNumber;
            this.originalInfo = new NodeInfo(Node.nodeInfo.getAddress().getHostAddress(),
                    this.connection.getPort(), Node.nodeInfo.getId());
            this.msgType = msgType;
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            new SendQuery(this.originalInfo, Node.successor, this.file.getFileId());

            //this.connection.acceptConnection();
            String receivedMsg = this.connection.start();
            //String receivedMsg = this.connection.receiveMessage();

            if (receivedMsg != null) {
                ReceivedQueryResponse queryResponse = new ReceivedQueryResponse(receivedMsg);
                NodeInfo destinationNodeInfo = new NodeInfo(queryResponse.getIP(), queryResponse.getPort(), queryResponse.getID());

                switch (this.msgType) {
                    case BACKUP -> {
                        this.connection.stop();
                        int port = Utils.getAvailablePort();
                        this.connection = new SSLServer("TLSv1.2", Node.nodeInfo.getAddress().getHostAddress(), port);
                        new SendNewConnection(new NodeInfo(Node.nodeInfo.getAddress().getHostAddress(), port, Node.nodeInfo.getId()), destinationNodeInfo);
                        System.out.println("WAITING FOR CONNECTION");
                        this.connection.start();
                        System.out.println("GOT CONNECTION");
                        new SendFile(this.file, this.replicationNumber, this.connection);
                    }
                    case DELETE -> new SendDeleteFile(Node.nodeInfo, destinationNodeInfo, this.file.getFileId());
                    case RESTORE -> { new SendAskRestoredFile(Node.nodeInfo, destinationNodeInfo, this.file.getFileId()); }
                    default -> System.err.println("Invalid message type in IssueMessage.");
                }
            } else {
                System.err.println("Null message found!");
                this.connection.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
            QUERY AO ID DO FICHEIRO -> Sender
            RECEBER A NODEINFO A CONTACTAR -> Listener
            SENDFILE PARA ESSA NODEINFO
         */
    }
}
