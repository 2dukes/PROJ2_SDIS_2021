package messages.ReceivedMessages;

import Threads.IssueMessage;
import chord.Node;
import macros.Macros;
import storage.PeerFileStored;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;

// IP_ORIG PORT_ORIG ID_ORIG RESTORED_FILE FILE_ID TRUE FILE_NAME CONTENT\n
// IP_ORIG PORT_ORIG ID_ORIG RESTORED_FILE FILE_ID FALSE\n
public class ReceivedRestoredFile extends Message {
    BigInteger fileId;
    String fileName;
    boolean hasContent;
    byte[] fileData;

    public ReceivedRestoredFile(String msg) {
        super(msg);
    }

    public void parseSpecificMessage() {
        this.fileId = new BigInteger(this.splitMsg[0]);
        this.hasContent = this.splitMsg[1].equals("TRUE");
        if (this.hasContent) {
            this.fileName = this.splitMsg[2];
            this.fileData = Base64.getDecoder().decode(this.splitMsg[3]);
        }
    }

    @Override
    public void run() {
        if (this.hasContent) {
            Node.storage.createRestoredFile(this.fileName, this.fileData);
        } else {
            // they didnt have the file, ask restored file to other node
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
                    new IssueMessage(peerFileDummy, 0, Macros.MSGTYPE.RESTORE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("No node available to restore file with ID: " + this.fileId);
            }
        }
    }
}
