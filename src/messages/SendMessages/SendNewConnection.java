package messages.SendMessages;

import Threads.ThreadPool;
import chord.NodeInfo;
import dispatchers.Sender;

// IP_ORIG PORT_ORIG ID_ORIG CONNECT
public class SendNewConnection {
    public SendNewConnection(NodeInfo currentSocketInfo, NodeInfo contactNodeInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append(currentSocketInfo.getAddress().getHostAddress()).append(" ");
        builder.append(currentSocketInfo.getPort()).append(" ");
        builder.append(currentSocketInfo.getId());
        builder.append(" CONNECT ").append("\n");

        ThreadPool.getInstance().execute(new Sender(contactNodeInfo.getAddress(), contactNodeInfo.getPort(), builder.toString()));
    }
}
