package messages.ReceivedMessages;

import java.math.BigInteger;

public abstract class Message implements Runnable {
    protected String msgType;
    protected String IP;
    protected int port;
    protected BigInteger ID;
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
        this.port = Integer.parseInt(splitMsg[1]);
        this.ID = new BigInteger(splitMsg[2]);
        this.msgType = splitMsg[3];

        String[] newMsg = new String[10];
        for (int i = 4; i < this.splitMsg.length; i++)
            newMsg[i-4] = this.splitMsg[i];

        this.splitMsg = newMsg;
    }

    public abstract void parseSpecificMessage();

    @Override
    public abstract void run();
}
