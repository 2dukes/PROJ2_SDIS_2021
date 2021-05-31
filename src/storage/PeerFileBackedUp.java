package storage;

import utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;
import java.security.NoSuchAlgorithmException;

public class PeerFileBackedUp extends PeerFile implements Cloneable, Serializable { //Files that the Peer asks others to back up
    private String path;

    public PeerFileBackedUp(String path, int replicationDeg) {
        this.path = path.trim();
        this.setName();
        this.replicationDeg = replicationDeg;
        this.readFile();
        this.computeChunks();
    }

    // Each file replication will have a different ID
    public BigInteger computeId(int replicationNumber) {
        try {
            this.fileId = Utils.hashID(this.fileSignature, replicationNumber);
            return this.fileId;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void readFile() {
        try {
            Path fPath = Paths.get(path);
            UserPrincipal fileOwner = Files.getOwner(fPath, LinkOption.NOFOLLOW_LINKS);
            String fileName = fPath.getFileName().toString();
            long lastModified = fPath.toFile().lastModified();

            this.fileSignature = fileName + fileOwner.getName() + lastModified;
            File file = new File(this.path);
            this.data = new byte[(int) file.length()];

            FileInputStream inputStream = new FileInputStream(file);
            inputStream.read(this.data);

            if(this.data.length == 0)
                System.err.println("Data is null sized");
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPath() {
        return path;
    }

    public void setName() {
        String[] pathSplit = this.path.split("/");
        this.fileName = pathSplit[pathSplit.length - 1];
    }

    @Override
    public PeerFileBackedUp clone() throws CloneNotSupportedException {
        return (PeerFileBackedUp)  super.clone();
    }
}
