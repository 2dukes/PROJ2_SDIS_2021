package Threads;

import chord.Node;
import chord.NodeInfo;
import macros.Macros;
import messages.ReceivedMessages.ReceivedQueryResponse;
import messages.SendMessages.*;
import sslengine.SSLServer;
import storage.PeerFile;
import utils.Utils;

import java.io.IOException;
import java.math.BigInteger;

public class IssueMessage implements Runnable {
    SSLServer connection;
    NodeInfo originalInfo;
    PeerFile file;
    int replicationNumber;
    Macros.MSGTYPE msgType;

    public IssueMessage(PeerFile file, int replicationNumber, Macros.MSGTYPE msgType) throws IOException {
        initIssueMessage(msgType);
        this.replicationNumber = replicationNumber;
        this.originalInfo = new NodeInfo(Node.nodeInfo.getAddress().getHostAddress(),
                this.connection.getPort(), Node.nodeInfo.getId());
        this.file = file;
    }

    public IssueMessage(Macros.MSGTYPE msgType) {
        initIssueMessage(msgType);
    }

    public void initIssueMessage(Macros.MSGTYPE msgType) {
        try {
            this.connection = new SSLServer(Macros.cypherSuite, Node.nodeInfo.getAddress().getHostAddress(), Utils.getAvailablePort(false));
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
            if(this.msgType != Macros.MSGTYPE.SUBSEQUENT_SUCCESSOR)
                new SendQuery(this.originalInfo, Node.successor, this.file.getFileId());
            else {
                BigInteger maxNumberOfNodes = new BigInteger(String.valueOf((int) Math.pow(2, Macros.numberOfBits)));
                new SendQuery(this.originalInfo, Node.successor, Node.successor.getId().add(BigInteger.ONE).mod(maxNumberOfNodes));
            }

            String receivedMsg = this.connection.start();

            if (receivedMsg != null) {
                ReceivedQueryResponse queryResponse = new ReceivedQueryResponse(receivedMsg);
                NodeInfo destinationNodeInfo = new NodeInfo(queryResponse.getIP(), queryResponse.getPort(), queryResponse.getID());

                switch (this.msgType) {
                    case SUBSEQUENT_SUCCESSOR -> Node.subsequentSuccessor = destinationNodeInfo;
                    case BACKUP -> {
                        this.connection.stop();
                        int port = Utils.getAvailablePort(false);
                        this.connection = new SSLServer(Macros.cypherSuite, Node.nodeInfo.getAddress().getHostAddress(), port);
                        new SendConnection(new NodeInfo(Node.nodeInfo.getAddress().getHostAddress(), port, Node.nodeInfo.getId()), destinationNodeInfo, "FILE_CONNECTION");
                        this.connection.start();
                        new SendFile(this.file, this.replicationNumber, this.connection);
                    }
                    case DELETE -> new SendDeleteFile(Node.nodeInfo, destinationNodeInfo, this.file.getFileId());
                    case RESTORE -> { new SendAskRestoredFile(Node.nodeInfo, destinationNodeInfo, this.file.getFileId()); }
                    default -> System.err.println("Invalid message type in IssueMessage.");
                }
            } else {
                this.connection.stop();
            }
        } catch (Exception ignored) { }

        /*
            QUERY AO ID DO FICHEIRO -> Sender
            RECEBER A NODEINFO A CONTACTAR -> Listener
            SENDFILE PARA ESSA NODEINFO
         */
    }
}
