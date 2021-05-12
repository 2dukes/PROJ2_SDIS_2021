package chord;

public class PeerFile {
    private String fileId;
    private byte[] bytes;
    private String path;

    // Local Node (Peer who asks for backup)
    public PeerFile(String path) {
        // get fileId
        // get bytes
    }

    // Successor Node (Peer that stores the file)
    public PeerFile(String fileId, byte[] bytes) {

    }
}
