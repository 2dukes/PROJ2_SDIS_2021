package chord;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NodeInfo {
    InetAddress address;
    int port;

    public NodeInfo(String IP, int port) {
        try {
            this.address = InetAddress.getByName(IP);
        } catch (UnknownHostException e) {
            System.err.println(e);
        }
        this.port = port;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }
}
