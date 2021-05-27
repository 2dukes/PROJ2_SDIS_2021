package messages.ReceivedMessages;

import Threads.ThreadPool;
import chord.AddNode;
import chord.Node;
import chord.NodeInfo;
import messages.SendMessages.SendRestoredFile;
import storage.PeerFileStored;

import java.io.IOException;
import java.math.BigInteger;

// IP_ORIG PORT_ORIG ID_ORIG ASK_RESTORED_FILE FILE_ID\n
public class ReceivedAskRestoredFile extends Message {
    BigInteger fileId;

    public ReceivedAskRestoredFile(String msg) {
        super(msg);
    }

    public void parseSpecificMessage() {
        this.fileId = new BigInteger(this.splitMsg[0]);
    }

    @Override
    public void run() {
        PeerFileStored fileStored = Node.storage.getStoredFile(this.fileId);

        NodeInfo nodeToContact = new NodeInfo(this.IP, this.port, this.ID);

        // IP_ORIG PORT_ORIG ID_ORIG RESTORED_FILE FILE_ID TRUE FILE_NAME CONTENT\n
        if (fileStored != null) {
            try {
                new SendRestoredFile(Node.nodeInfo, nodeToContact, fileStored, "TRUE");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { // IP_ORIG PORT_ORIG ID_ORIG RESTORED_FILE FILE_ID FALSE\n
            PeerFileStored peerFileDummy = new PeerFileStored(
                    this.fileId,
                    "",
                    null,
                    0,
                    0
            );

            try {
                new SendRestoredFile(Node.nodeInfo, nodeToContact, peerFileDummy, "FALSE");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
