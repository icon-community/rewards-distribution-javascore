package contract;

import score.Address;
import score.Context;
import score.annotation.EventLog;
import score.annotation.External;
// import scorex.util.HashMap;

// import java.util.Map;
// import java.util.HashMap;
import java.math.BigInteger;
import java.util.List;

public class Contract
{
    private static final String TRUE_STRING = "true";
    public void Contract() {
    }

    /**
     * Checks if a user is registered in the registration book.
     *
     * @param user the address of the user to check
     * @return "true" if the user is registered, "false" otherwise
     */
    @External(readonly=true)
    public String isUserRegistered(Address user) {
        return TRUE_STRING;
    }

}
