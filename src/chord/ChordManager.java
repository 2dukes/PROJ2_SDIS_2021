package chord;

import utils.Utils;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

public class ChordManager {

    public void addNode(String IP, int port) {
        // calcular ID, a partir do IP e da port
        BigInteger id = null;
        try {
            id = Utils.hashID(new NodeInfo(IP, port));
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }

        // TODO: is buscar o IP e a porta do primeiro node, que está (num ficheiro)?? visível para todos
        // ir buscar a fingerTable desse node

        FingerTable fingerTable = new FingerTable(); // TODO: change to first node finger table

        boolean foundDeridedPlace = false;
        while (!foundDeridedPlace) {
            BigInteger maxId = fingerTable.getMaxId();

            if (maxId.compareTo(id) < 0) //
                continue;

            // percorrer a fingerTable para encontrar o lugar no novo node

            ConcurrentHashMap<BigInteger, NodeInfo> fingerTableMap = fingerTable.getFingerTable();
            List<BigInteger> fingerTableKeys = new ArrayList<BigInteger>(fingerTableMap.keySet());
            // Collections.sort(fingerTableKeys);

            for (BigInteger nodeId: fingerTableMap.keySet()) {
                if (nodeId.compareTo(id) > 0) {


                }
            }




        }

        // Verificar se não é o próprio nó
        // Senão pegar num node -> percorrer fingerTable -> ir para o node com ID mais próximo do novo node,
            // repetir, até encontrarmos o node com ID mais próximo do novo ID

        // ajustar os sucessores e antecedores do nó anterior e do próximo
        // ajustar a localização dos ficheiros do próximo nó
    }

    public void removeNode(String ID) {

    }
}
