package messages.SendMessages;

import Threads.ThreadPool;
import chord.NodeInfo;
import dispatchers.Sender;

// IP_ORIG PORT_ORIG ID_ORIG RESTORED_CONNECTION
public class SendNewRestoredConnection {
    public SendNewRestoredConnection(NodeInfo currentSocketInfo, NodeInfo contactNodeInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append(currentSocketInfo.getAddress().getHostAddress()).append(" ");
        builder.append(currentSocketInfo.getPort()).append(" ");
        builder.append(currentSocketInfo.getId());
        builder.append(" RESTORED_CONNECTION ").append("\n");

        ThreadPool.getInstance().execute(new Sender(contactNodeInfo.getAddress(), contactNodeInfo.getPort(), builder.toString()));
    }
}
