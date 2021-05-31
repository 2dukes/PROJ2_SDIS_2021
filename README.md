# SDIS Project

SDIS Project for group T4G26.

Group members:

1. Nuno Castro Silva (<up201404676@edu.fe.up.pt>)
2. Rui Filipe Mendes Pinto (<up201806441@edu.fe.up.pt>)
3. Tiago Gonçalves Gomes (<up201806658@edu.fe.up.pt>)

## Second Project
**Note**: The folders where the scripts have to be executed follow the same logic as in the first project.

### Start RMI (build/ folder)
```
rmiregistry
```

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



