package messages.SendMessages;

import Threads.ThreadPool;
import chord.NodeInfo;
import dispatchers.Sender;
import storage.PeerFileStored;

import java.io.IOException;
import java.util.Base64;

// IP_ORIG PORT_ORIG ID_ORIG RESTORED_FILE FILE_ID TRUE FILE_NAME CONTENT\n
// IP_ORIG PORT_ORIG ID_ORIG RESTORED_FILE FILE_ID FALSE\n
public class SendRestoredFile {
    public SendRestoredFile(NodeInfo currentNodeInfo, NodeInfo contactNodeInfo, PeerFileStored peerFile, String hasFile) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(currentNodeInfo.getAddress().getHostAddress()).append(" ");
        builder.append(currentNodeInfo.getPort()).append(" ");
        builder.append(currentNodeInfo.getId());
        builder.append(" RESTORED_FILE ");
        builder.append(peerFile.getFileId()).append(" ");
        builder.append(hasFile);
        if(hasFile.equals("TRUE")) {
            builder.append(" ").append(peerFile.getName()).append(" ");
            builder.append(new String(Base64.getEncoder().encode(peerFile.getData()))).append("\n");
        }

        ThreadPool.getInstance().execute(new Sender(contactNodeInfo.getAddress(), contactNodeInfo.getPort(), builder.toString()));
    }
}
