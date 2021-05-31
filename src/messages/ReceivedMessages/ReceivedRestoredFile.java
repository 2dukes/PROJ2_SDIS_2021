package messages.ReceivedMessages;

import Threads.IssueMessage;
import chord.Node;
import macros.Macros;
import storage.PeerFileStored;

import java.io.IOException;
import java.math.BigInteger;

// IP_ORIG PORT_ORIG ID_ORIG RESTORED_FILE FILE_ID FILE_NAME REPLICATION_DEG REPLICATION_NUMBER CONTENT\n
// IP_ORIG PORT_ORIG ID_ORIG RESTORED_FILE FILE_ID\n
public class ReceivedRestoredFile extends Message {
    BigInteger fileId;

    public ReceivedRestoredFile(String msg) {
        super(msg);
    }

    public void parseSpecificMessage() {
        this.fileId = new BigInteger(this.splitMsg[0]);
    }

    @Override
    public void run() {
        if(Node.fileIdsConsultedForRestore.size() > 1) {
            Node.fileIdsConsultedForRestore.remove(0);
            PeerFileStored peerFileDummy = new PeerFileStored(
                    Node.fileIdsConsultedForRestore.get(0),
                    "",
                    null,
                    0,
                    0
            );

            try {
                new IssueMessage(peerFileDummy, 0, Macros.MSGTYPE.RESTORE).run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("No node available to restore file with ID: " + this.fileId);
        }
    }
}
