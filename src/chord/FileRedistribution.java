package chord;

import macros.Macros;
import messages.SendMessages.SendConnection;
import messages.SendMessages.SendFile;
import sslengine.SSLServer;
import storage.PeerFileStored;
import utils.Utils;

import java.math.BigInteger;
import java.util.List;

public class FileRedistribution implements Runnable {
    NodeInfo newPredecessor;
    SSLServer connection;

    public FileRedistribution(NodeInfo newPredecessor) {
        this.newPredecessor = newPredecessor;
    }

    public void redistributeFile(BigInteger currentFileId) {
        try {
            int port = Utils.getAvailablePort(false);
            String IP = Node.nodeInfo.getAddress().getHostAddress();
            this.connection = new SSLServer(Macros.cypherSuite, IP, port);
            new SendConnection(new NodeInfo(IP, port, Node.nodeInfo.getId()), this.newPredecessor, "FILE_CONNECTION");
            this.connection.start();
            PeerFileStored peerFile = Node.storage.getStoredFile(currentFileId);
            new SendFile(peerFile, peerFile.getReplicationNumber(), this.connection);
            Node.storage.removeStoredFile(currentFileId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        List<BigInteger> fileIds = Node.storage.getFilesStoredIds();
        BigInteger maxNumberOfNodes = new BigInteger(String.valueOf((int) Math.pow(2, Macros.numberOfBits)));

        for (BigInteger currentFileId: fileIds) {
            if(Node.predecessor.getId().compareTo(Node.nodeInfo.getId()) < 0 && // Não deu a volta
                    Node.predecessor.getId().compareTo(currentFileId) <= 0
                    && currentFileId.compareTo(this.newPredecessor.getId()) <= 0){
                redistributeFile(currentFileId);
            }
            else if(Node.predecessor.getId().compareTo(Node.nodeInfo.getId()) >= 0) { // Deu a volta
                if(this.newPredecessor.getId().compareTo(Node.predecessor.getId()) > 0 && this.newPredecessor.getId().compareTo(maxNumberOfNodes) < 0) {
                    if(currentFileId.compareTo(this.newPredecessor.getId()) <= 0 && currentFileId.compareTo(Node.predecessor.getId()) > 0) {
                        redistributeFile(currentFileId);
                    }
                } else if(currentFileId.compareTo(this.newPredecessor.getId()) <= 0 ||
                        currentFileId.compareTo(Node.predecessor.getId()) > 0) {
                    redistributeFile(currentFileId);
                }
            }
        }
    }
}
