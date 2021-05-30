package messages.ReceivedMessages;

import chord.Node;

import java.math.BigInteger;

// IP_ORIG PORT_ORIG ID_ORIG DELETE_FILE FILE_ID\n
public class ReceivedDeleteFile extends Message {
    BigInteger fileId;

    public ReceivedDeleteFile(String msg) {
        super(msg);
    }

    @Override
    public void parseSpecificMessage() {
        this.fileId = new BigInteger(this.splitMsg[0]);
    }

    @Override
    public void run() {
        Node.storage.removeStoredFile(this.fileId);
    }
}
