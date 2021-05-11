package messages.ReceivedMessages;

import java.math.BigInteger;

// IP_RESPONSE PORT_RESPONSE ID_RESPONSE QUERY_RESPONSE LOOKUP_ID LOOKEDUP_ID -> Response
public class ReceivedQueryResponse extends Message {

    BigInteger lookedUpId;
    BigInteger lookupId;

    public ReceivedQueryResponse(String msg) {
        super(msg);
    }

    public void parseSpecificMessage() {
        this.lookedUpId = new BigInteger(this.splitMsg[0]);
        this.lookupId = new BigInteger(this.splitMsg[1]);
    }

    @Override
    public void run() {

    }
}
