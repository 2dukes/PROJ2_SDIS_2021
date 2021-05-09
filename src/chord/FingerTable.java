package chord;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;

public class FingerTable {
    private ConcurrentHashMap<BigInteger, NodeInfo> fingerTable;

    public FingerTable() {
        this.fingerTable = new ConcurrentHashMap<>();
    }
    
    public void addNode(BigInteger id, NodeInfo nodeInfo) {
        this.fingerTable.put(id, nodeInfo);
    }

    public NodeInfo getNodeInfo(BigInteger id) {
        return (NodeInfo) this.fingerTable.get(id);
    }

    public void removeNode(BigInteger id) {
        this.fingerTable.remove(id);
    }

    public BigInteger getMaxId() {
        BigInteger maxId = null;
        for (BigInteger id : fingerTable.keySet()) {
            if (maxId == null || id.compareTo(maxId) > 0)
                maxId = id;
        }
        return maxId;
    }

    public ConcurrentHashMap<BigInteger, NodeInfo> getFingerTable() {
        return this.fingerTable;
    }
}
