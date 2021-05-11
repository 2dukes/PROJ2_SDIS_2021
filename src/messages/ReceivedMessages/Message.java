package messages.ReceivedMessages;

import java.math.BigInteger;

public abstract class Message implements Runnable {
    protected String msgType;
    protected String IP;
    protected String port;
    protected String ID;
    protected String[] splitMsg;
    // String IP, String port, BigInteger ID, String msgType
    public Message(String msg) {
        try {
            parseCommonMessage(msg);
            parseSpecificMessage();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void parseCommonMessage(String msg) {
        this.splitMsg = msg.trim().split("\\s+");
        this.IP = splitMsg[0];
        this.port = splitMsg[1];
        this.ID = splitMsg[2];
        this.msgType = splitMsg[3];
    }

    public abstract void parseSpecificMessage();

    @Override
    public abstract void run();
}
