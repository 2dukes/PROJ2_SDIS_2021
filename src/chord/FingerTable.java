package chord;

import macros.Macros;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FingerTable {
    private ConcurrentHashMap<BigInteger, NodeInfo> fingerTable;
    private List<BigInteger> keysOrder;

    public FingerTable() {
        this.fingerTable = new ConcurrentHashMap<>();
        this.keysOrder = new ArrayList<>();

        BigInteger maxNumberOfNodes = new BigInteger(String.valueOf((int) Math.pow(2, Macros.numberOfBits)));
        for (int i = 0; i < Macros.numberOfBits; i++) {
            BigInteger newCurrentId = Node.nodeInfo.getId().add(new BigInteger(String.valueOf((int) Math.pow(2, i))))
                    .mod(maxNumberOfNodes);

            this.keysOrder.add(newCurrentId);
            this.addNode(newCurrentId, Node.nodeInfo);
        }
    }
    
    public void addNode(BigInteger id, NodeInfo nodeInfo) {
        this.fingerTable.put(id, nodeInfo);
    }

    public NodeInfo getNodeInfo(BigInteger id) {
        return this.fingerTable.get(id);
    }

    public void removeNode(BigInteger id) {
        this.fingerTable.remove(id);
    }

    public ConcurrentHashMap<BigInteger, NodeInfo> getFingerTable() {
        return this.fingerTable;
    }

    public List<BigInteger> getKeysOrder() { return keysOrder; }

    public BigInteger getLastKey() { return this.keysOrder.get(this.keysOrder.size() - 1); }
}
