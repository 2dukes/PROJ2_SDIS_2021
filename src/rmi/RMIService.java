package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIService extends Remote {
    void backup(String path, int replicationDeg) throws RemoteException;
    void delete(String path) throws RemoteException;
}
