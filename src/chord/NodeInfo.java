package chord;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.math.BigInteger;


public class NodeInfo {
    private InetAddress address;
    private int port;

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
