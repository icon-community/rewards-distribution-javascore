package contract;

import score.Address;
import score.Context;
import score.annotation.External;
import score.annotation.Payable;
import score.annotation.EventLog;

import java.math.BigInteger;
import java.util.Map;

public class Contract
{
    // Mapping of user addresses to their claimable ICX amounts
    private static final String CLAIMS = "claims";
    private static final IterableDictDB<Address, BigInteger> claims = new IterableDictDB<>(CLAIMS, BigInteger.class, Address.class, false);

    // Add claimable ICX for a user
    @External
    public void addClaim(Address user, BigInteger amount) {
        // Ensure only the owner can add claims
        if (!Context.getCaller().equals(Context.getOwner())) {
            Context.revert("Only the contract owner can add claims");
        }

        // Update the claimable amount for the user
        BigInteger currentAmount = claims.getOrDefault(user, BigInteger.ZERO);
        claims.set(user, currentAmount.add(amount));
        ClaimAdded(user, amount);
    }

    // Allow users to claim their ICX
    @External
    public void claim() {
        Address user = Context.getCaller();
        BigInteger claimAmount = claims.getOrDefault(user, BigInteger.ZERO);

        // Ensure there is something to claim
        if (claimAmount.equals(BigInteger.ZERO)) {
            Context.revert("No ICX to claim");
        }

        // Clear the claimable amount for the user
        claims.set(user, BigInteger.ZERO);

        // Transfer ICX to the user
        Context.transfer(user, claimAmount);

        // Emit the Claimed event
        Claimed(user, claimAmount);
    }

    // Allow owner to claim ICX from contract
    @External
    public void ownerClaim(BigInteger amount) {
        // Ensure only the owner can execute this function
        if (!Context.getCaller().equals(Context.getOwner())) {
            Context.revert("Only the contract owner can execute this function");
        }
        Address owner = Context.getOwner();

        // Transfer ICX to the user
        Context.transfer(owner, amount);

        // Emit the Claimed event
        OwnerClaimed(owner, amount);
    }

    // Get the claimable ICX amount for a user
    @External(readonly=true)
    public BigInteger getClaimableAmount(Address user) {
        return claims.getOrDefault(user, BigInteger.ZERO);
    }

    @EventLog(indexed=2)
    public void ClaimAdded(Address user, BigInteger amount) {}

    @EventLog(indexed=2)
    public void Claimed(Address user, BigInteger amount) {}

    @EventLog(indexed=2)
    public void OwnerClaimed(Address owner, BigInteger amount) {}
}
