package chord;

import jdk.swing.interop.SwingInterOpUtils;
import macros.Macros;

import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;
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

    /*public boolean checkIfInsideFT(BigInteger lookUpId) {
        boolean turned = false;
        BigInteger leftSideInterval, rightSideInterval;
        if (lookUpId.compareTo(Node.nodeInfo.getId()) < 0)
            turned = true;
        for (BigInteger i : Node.fingerTable.getKeysOrder()) {
            if(i.compareTo(Node.nodeInfo.getId()) > 0) {
                leftSideInterval = Node.nodeInfo.getId();
                rightSideInterval = i;
                if(lookUpId.compareTo(leftSideInterval) >= 0 && lookUpId.compareTo(rightSideInterval) <= 0)
                    return true; // O valor de procura está na FT
            } else {
                leftSideInterval = BigInteger.ZERO;
                rightSideInterval = i;
                if(lookUpId.compareTo(leftSideInterval) >= 0 && lookUpId.compareTo(rightSideInterval) <= 0)
                    return true; // O valor de procura está na FT
            }
        }
    }

    public BigInteger getMaxId() {
        BigInteger maxId = null;
        for (BigInteger id : fingerTable.keySet()) {
            if (maxId == null || id.compareTo(maxId) > 0)
                maxId = id;
        }
        return maxId;
    }*/

    public ConcurrentHashMap<BigInteger, NodeInfo> getFingerTable() {
        return this.fingerTable;
    }

    public List<BigInteger> getKeysOrder() { return keysOrder; }
}
