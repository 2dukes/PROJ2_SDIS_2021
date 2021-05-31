package macros;

public interface Macros {
    int numberOfBits = 4;
    int gatePort = 6969;
    String cypherSuite = "TLSv1.2";


    enum MSGTYPE {
        BACKUP,
        DELETE,
        RESTORE,
        SUBSEQUENT_SUCCESSOR
    }
}
