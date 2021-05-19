package Threads;

import chord.Node;
import chord.NodeInfo;
import jsse.JSSEServerConnection;
import messages.ReceivedMessages.ReceivedQueryResponse;
import messages.SendMessages.SendFile;
import messages.SendMessages.SendQuery;
import storage.PeerFile;
import storage.PeerFileBackedUp;

import java.io.IOException;
import java.math.BigInteger;

public class IssueMessage implements Runnable {
    JSSEServerConnection connection;
    NodeInfo originalInfo;
    PeerFile file;
    int replicationNumber;

    public IssueMessage(PeerFile file, int replicationNumber) throws IOException {
        this.connection = new JSSEServerConnection();
        this.file = file;
        this.replicationNumber = replicationNumber;
        this.originalInfo = new NodeInfo(Node.nodeInfo.getAddress().getHostAddress(),
                this.connection.getPort(), Node.nodeInfo.getId());
    }

    @Override
    public void run() {
        try {
            new SendQuery(this.originalInfo, Node.successor, this.file.getFileId());

            this.connection.acceptConnection();
            String receivedMsg = this.connection.receiveMessage();
            ReceivedQueryResponse queryResponse = new ReceivedQueryResponse(receivedMsg);
            NodeInfo destinationNodeInfo = new NodeInfo(queryResponse.getIP(), queryResponse.getPort(), queryResponse.getID());

            new SendFile((PeerFileBackedUp) this.file, this.replicationNumber, destinationNodeInfo);

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
