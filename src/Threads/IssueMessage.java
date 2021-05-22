package Threads;

import chord.Node;
import chord.NodeInfo;
import jsse.JSSEServerConnection;
import macros.Macros;
import messages.ReceivedMessages.ReceivedQueryResponse;
import messages.SendMessages.SendDeleteFile;
import messages.SendMessages.SendFile;
import messages.SendMessages.SendQuery;
import storage.PeerFile;

import java.io.IOException;

public class IssueMessage implements Runnable {
    JSSEServerConnection connection;
    NodeInfo originalInfo;
    PeerFile file;
    int replicationNumber;
    Macros.MSGTYPE msgType;

    public IssueMessage(PeerFile file, int replicationNumber, Macros.MSGTYPE msgType) throws IOException {
        this.connection = new JSSEServerConnection();
        this.file = file;
        this.replicationNumber = replicationNumber;
        this.originalInfo = new NodeInfo(Node.nodeInfo.getAddress().getHostAddress(),
                this.connection.getPort(), Node.nodeInfo.getId());
        this.msgType = msgType;
    }

    @Override
    public void run() {
        try {
            new SendQuery(this.originalInfo, Node.successor, this.file.getFileId());

            this.connection.acceptConnection();
            String receivedMsg = this.connection.receiveMessage();
            ReceivedQueryResponse queryResponse = new ReceivedQueryResponse(receivedMsg);
            NodeInfo destinationNodeInfo = new NodeInfo(queryResponse.getIP(), queryResponse.getPort(), queryResponse.getID());

            switch (this.msgType) {
                case BACKUP -> new SendFile(this.file, this.replicationNumber, destinationNodeInfo);
                case DELETE -> new SendDeleteFile(Node.nodeInfo, destinationNodeInfo, this.file.getFileId());
                default -> System.err.println("Invalid message type in IssueMessage.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
            QUERY AO ID DO FICHEIRO -> Sender
            RECEBER A NODEINFO A CONTACTAR -> Listener
            SENDFILE PARA ESSA NODEINFO
         */
    }
}
