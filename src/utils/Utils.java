package utils;

import macros.Macros;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    public static BigInteger hashID(String str, int integ) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String seed = str + ":" + integ;
        byte[] hash = md.digest(seed.getBytes(StandardCharsets.UTF_8));

        BigInteger number = new BigInteger(1, hash).mod(new BigInteger(String.valueOf((int) Math.pow(2, Macros.numberOfBits))));
        return number;
    }

    public static Integer getAvailablePort(boolean canTestGate) throws IOException {
        if(canTestGate) {
            try {
                ServerSocket s = new ServerSocket(Macros.gatePort);
                s.close();
                return Macros.gatePort;
            } catch (IOException e) {
                ServerSocket s = new ServerSocket(0);
                s.close();
                return s.getLocalPort();
            }
        } else {
            ServerSocket s;
            do {
                s = new ServerSocket(0);
                s.close();
            } while(Macros.gatePort == s.getLocalPort());

            return s.getLocalPort();
        }

    }
}
