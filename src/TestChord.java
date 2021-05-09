public class TestChord {
    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore","../../keys/truststore"); // CLIENT TRUST STORE
        System.setProperty("javax.net.ssl.trustStoreType","JKS");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        System.setProperty("javax.net.ssl.keyStore","../../keys/server.keys");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");

        System.out.println("Test");
    }
}
