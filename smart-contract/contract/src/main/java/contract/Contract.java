package contract;

import score.Address;
import score.Context;
import score.DictDB;
import score.annotation.External;
import score.annotation.Payable;
import score.annotation.EventLog;

import java.math.BigInteger;
import java.util.Map;
import java.util.List;

/*
 * This contract allows admins to add claimable ICX amounts for users.
 */
public class Contract
{
    // Mapping of user addresses to their claimable ICX amounts
    private static final String CLAIMS = "claims";
    private static final String ADMINS = "admins";
    private static final IterableDictDB<Address, BigInteger> claims = new IterableDictDB<>(CLAIMS, BigInteger.class, Address.class, false);
    private static final DictDB<Address, Boolean> admins = Context.newDictDB(ADMINS, Boolean.class);

    /*
     * Constructor that adds the contract owner as an admin.
     */
    public Contract() {
        // Add the contract owner as an admin
        Address ownerAddress = Context.getOwner();

        // check if the admin is already added
        if (admins.getOrDefault(ownerAddress, false)) {
            return;
        }

        // add the owner as an admin
        admins.set(ownerAddress, true);

        // Emit the AdminAdded event
        AdminAdded(ownerAddress);
    }

    /*
     * Add an admin to the contract.
     */
    @External
    public void addAdmin(Address admin) {
        // Ensure only the owner can add admins
        Address caller = Context.getCaller();

        Context.require(caller.equals(Context.getOwner()), "Only the contract owner can add admins");

        // check if the admin is already added
        if (admins.getOrDefault(admin, false)) {
            return;
        }

        // add new admin
        admins.set(admin, true);

        // Emit the AdminAdded event
        AdminAdded(admin);
    }

    /*
     * Check if address is admin
     */
    @External(readonly=true)
    public Boolean isAdmin(Address account) {
        return admins.getOrDefault(account, false);
    }

    /*
     * Remove an admin from the contract.
     */
    @External
    public void removeAdmin(Address admin) {
        // Ensure only the owner can remove admins
        Address caller = Context.getCaller();

        Context.require(caller.equals(Context.getOwner()), "Only the contract owner can remove admins");

        // check if the admin is already added
        if (!admins.getOrDefault(admin, false)) {
            return;
        }

        // remove admin
        admins.set(admin, false);

        // Emit the AdminRemoved event
        AdminRemoved(admin);
    }

    /*
     * Add a claimable ICX amount for a user.
     */
    @External
    public void addClaim(Address user, BigInteger amount) {
        // Ensure only admins can add claims
        Contract contractInstance = new Contract();
        onlyAdmins(contractInstance);

        // Update the claimable amount for the user
        BigInteger currentAmount = claims.getOrDefault(user, BigInteger.ZERO);
        claims.set(user, currentAmount.add(amount));
        ClaimAdded(user, amount);
    }

    /*
     * Claim the ICX amount for the caller.
     */
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

    /*
     * Claim the ICX amount by an admin.
     */
    @External
    public void adminClaim(BigInteger amount) {
        // Ensure only admins can claim ICX
        Contract contractInstance = new Contract();
        onlyAdmins(contractInstance);

        Address caller = Context.getCaller();
        Address owner = Context.getOwner();

        // Transfer ICX to the user
        Context.transfer(owner, amount);

        // Emit the Claimed event
        OwnerClaimed(caller, amount);
    }

    public static void onlyAdmins(Contract contractInstance) {

        Address caller = Context.getCaller();
        Context.require(contractInstance.admins.getOrDefault(caller, false), "Only admins can execute this function");
    }

    /*
     * Return the claimable ICX amount for a user.
     */
    @External(readonly=true)
    public BigInteger getClaimableAmount(Address user) {
        return claims.getOrDefault(user, BigInteger.ZERO);
    }

    /*
     * Fallback function to allow the contract to receive ICX.
     */
    @Payable
    public void fallback() {
    }

    /*
     * Event emitted when an admin is added.
     */
    @EventLog(indexed=1)
    public void AdminAdded(Address admin) {}

    /*
     * Event emitted when an admin is Removed.
     */
    @EventLog(indexed=1)
    public void AdminRemoved(Address admin) {}

    /*
     * Event emitted when a claim is added.
     */
    @EventLog(indexed=2)
    public void ClaimAdded(Address user, BigInteger amount) {}

    /*
     * Event emitted when a claim is made by a user.
     */
    @EventLog(indexed=2)
    public void Claimed(Address user, BigInteger amount) {}

    /*
     * Event emitted when a claim is made by an admin.
     */
    @EventLog(indexed=2)
    public void OwnerClaimed(Address owner, BigInteger amount) {}
}
