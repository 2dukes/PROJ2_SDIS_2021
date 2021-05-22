package macros;

public interface Macros {
    int numberOfBits = 4;
    int gatePort = 6969;

    enum MSGTYPE {
        BACKUP,
        DELETE,
        RESTORE,
        STATUS
    }
}
