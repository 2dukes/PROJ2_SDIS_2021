package chord;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class NodeInfo implements Serializable {
    private InetAddress address;
    private int port;
    private BigInteger id;

    public NodeInfo(String IP, int port, BigInteger id) {
        try {
            this.address = InetAddress.getByName(IP);
        } catch (UnknownHostException e) {
            System.err.println(e);
        }
        this.port = port;
        this.id = id;
    }

    public BigInteger getId() {
        return this.id;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeInfo nodeInfo = (NodeInfo) o;
        return java.util.Objects.equals(id, nodeInfo.id);
    }

    @Override
    public String toString() {
        return "IP=" + this.address.getHostAddress() + " Port=" + this.port + " ID=" + this.id;
    }

    public void setId(BigInteger id) { this.id = id; }
}
