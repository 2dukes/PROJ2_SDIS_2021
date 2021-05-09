package utils;

import chord.NodeInfo;
import macros.Macros;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.rmi.AlreadyBoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    public static BigInteger hashID(NodeInfo nodeInfo) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String seed = nodeInfo.getAddress().getHostAddress() + ":" + nodeInfo.getPort();
        byte[] hash = md.digest(seed.getBytes(StandardCharsets.UTF_8));

        BigInteger number = new BigInteger(1, hash).mod(Macros.numberOfBits.pow(2));
        return number;
    }

    public static Integer getAvailablePort() throws IOException {
        try {
            ServerSocket s = new ServerSocket(8000);
            return 8000;
        } catch(IOException e) {
            ServerSocket s = new ServerSocket(0);
            return s.getLocalPort();
        }
    }
}
