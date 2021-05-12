package chord;

import macros.Macros;
import messages.SendMessages.SendQuery;

import java.io.IOException;
import java.math.BigInteger;

public class BuildFingerTable implements Runnable {

    public BuildFingerTable() {}

    @Override
    public void run() {
        BigInteger currentId = Node.nodeInfo.getId();
        for (int i = 0; i < Macros.numberOfBits; i++) {
            BigInteger newCurrentId = currentId.add(new BigInteger(String.valueOf((int) Math.pow(i, 2)))); // TODO: check if conversion from double to BigInteger is correct

            if (newCurrentId.compareTo(Node.successor.getId()) <= 0) {
                Node.addToFingerTable(Node.successor.getId(), Node.successor);
            } else { // successor does the same thing to its successor, and so on...
                try {
                    new SendQuery(Node.nodeInfo, Node.successor, newCurrentId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
