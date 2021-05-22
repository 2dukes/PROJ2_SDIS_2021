package chord;

import macros.Macros;
import messages.SendMessages.SendFile;
import storage.PeerFileStored;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class FileRedistribution implements Runnable {
    NodeInfo newPredecessor;

    public FileRedistribution(NodeInfo newPredecessor) {
        this.newPredecessor = newPredecessor;
    }

    public void redistributeFile(BigInteger currentFileId) {
        try {
            PeerFileStored peerFile = Node.storage.getStoredFile(currentFileId);
            new SendFile(peerFile, peerFile.getReplicationNumber(), this.newPredecessor);
            Node.storage.removeStoredFile(currentFileId);
        } catch (IOException e) {
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
            else if(Node.predecessor.getId().compareTo(Node.nodeInfo.getId()) > 0) { // Deu a volta
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
