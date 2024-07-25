package contract;

import score.Address;
import score.Context;
import score.annotation.External;
import score.annotation.EventLog;

import java.math.BigInteger;

/*
 * This contract allows admins to add claimable ICX amounts for users.
 */
public class MockToken
{
    @External
    public void transfer(Address to, BigInteger amount) {
        // Ensure only the owner can remove admins
        Address caller = Context.getCaller();

        // Emit the AdminRemoved event
        Transfer(to, caller, amount);
    }

    /*
     * Event emitted when transfer is called.
     */
    @EventLog(indexed=2)
    public void Transfer(Address to, Address from, BigInteger amount) {}
}
