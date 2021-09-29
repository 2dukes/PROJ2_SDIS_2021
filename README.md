# SDIS Project

SDIS Project for group T4G26.

Group members:

1. Nuno Castro Silva (<up201404676@edu.fe.up.pt>)
2. Rui Filipe Mendes Pinto (<up201806441@edu.fe.up.pt>)
3. Tiago Gonçalves Gomes (<up201806658@edu.fe.up.pt>)

## Project - Distributed Backup Service

This project consisted of a Distributed peer-to-peer (P2P) system that relied on the Chord Protocol and allowed backup, deletion, and restoral of a file previously divided in chunks that were spread among the other peers. The messages exchanged between the peers were encrypted through an SSL communication channel. The project was built to be fault-tolerant and scalable, always maintaining its stability. Lastly, the usage of thread pools and non-blocking I/O allowed the system to achieve high levels of concurrency and parallelism.

### Start RMI (build/ folder)
```
rmiregistry
```

### Cleanup (build/ folder)
```
sh ../../scripts/cleanup.sh [ID]
```
Eg.: sh ../../scripts/cleanup.sh 3

### Compile (src/ folder)
``` 
sh ../scripts/compile.sh
```

### Run (build/ folder)

#### Peer
```
sh ../../scripts/peer.sh <ACCESS_POINT> [ID]
```
Eg.: sh ../../scripts/peer.sh GateNode 8

#### Backup Protocol
```
sh ../../scripts/test.sh <ACCESS_POINT> BACKUP <PATH_TO_FILE> <REPLICATION_DEG>
```
Eg.: sh ../../scripts/test.sh GateNode BACKUP ../../resources/files/hello.txt 2

#### Restore Protocol
```
sh ../../scripts/test.sh <ACCESS_POINT> RESTORE <PATH_TO_FILE>
```
Eg.: sh ../../scripts/test.sh GateNode RESTORE ../../resources/files/hello.txt

#### Delete Protocol
```
sh ../../scripts/test.sh <ACCESS_POINT> DELETE <PATH_TO_FILE>
```
Eg.: sh ../../scripts/test.sh GateNode DELETE ../../resources/files/hello.txt

### Notes:
> To change the Chord's Network size, modify the numberOfBits variable in the Macros file.\
> The Java version used for this project during development was 15.0.2. 
