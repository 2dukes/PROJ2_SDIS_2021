package chord;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class ChordManager {

    public void addNode(BigInteger id) {
        // calcular ID, a partir do IP e da port

        // TODO: ir buscar o IP e a porta do primeiro node(=GATE), que está visível para todos
        // ir buscar a fingerTable desse node

        FingerTable fingerTable = new FingerTable(); // TODO: change to first node finger table

        while (true) {
            BigInteger maxId = fingerTable.getMaxId();

            if (maxId.compareTo(id) < 0) {
                //node a adicionar está além do maxID deste node

                // IP PORT ADDNODE ID
                fingerTable = getNextFingerTable(maxId);
                continue;
            }

            // percorrer a fingerTable para encontrar o lugar no novo node

            ConcurrentHashMap<BigInteger, NodeInfo> fingerTableMap = fingerTable.getFingerTable();
            List<BigInteger> fingerTableKeys = new ArrayList<>(fingerTableMap.keySet());
            Collections.sort(fingerTableKeys);

            for (int i = 0; i < fingerTableKeys.size(); i++) {

                if (fingerTableKeys.get(i).compareTo(id) == 0) {
                    // node a adicionar na posiçao id já existe nessa pos
                }

                if (fingerTableKeys.get(i).compareTo(id) > 0) {

                    // mudar o antecessor do proximo node (nodeID)
                    // mudar o sucessor no node anterior (nodeID anterior)
                    // atualizar a localização dos ficheiros do próximo node

                    // fazer set do sucessor -> sucessor = próximo node
                    // fazer set no antecessor -> antecessor = node anterior

                }
            }
            break;
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
