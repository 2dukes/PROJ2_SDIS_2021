package utils;

import chord.NodeInfo;
import macros.Macros;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.rmi.AlreadyBoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    public static BigInteger hashID(String IP, int port) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String seed = IP + ":" + port;
        byte[] hash = md.digest(seed.getBytes(StandardCharsets.UTF_8));

        BigInteger number = new BigInteger(1, hash).mod(new BigInteger(String.valueOf((int) Math.pow(Macros.numberOfBits, 2))));
        return number;
    }

    public static Integer getAvailablePort() throws IOException {
        try {
            ServerSocket s = new ServerSocket(8000);
            s.close();
            return 8000;
        } catch(IOException e) {
            ServerSocket s = new ServerSocket(0);
            s.close();
            return s.getLocalPort();
        }
    }
}
