package messages.ReceivedMessages;

import java.math.BigInteger;

// IP_ORIG PORT_ORIG ID_ORIG QUERY LOOKUP_ID -> Request
public class ReceivedQuery extends Message {

    BigInteger lookupId;

    public ReceivedQuery(String msg) {
        super(msg);
    }

    public void parseSpecificMessage() {
        this.lookupId = new BigInteger(this.splitMsg[0]);
    }

    @Override
    public void run() {

    }
}
