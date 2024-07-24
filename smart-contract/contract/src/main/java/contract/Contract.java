package contract;

import score.Address;
import score.Context;
import score.DictDB;
import score.BranchDB;
import score.VarDB;
import score.annotation.External;
import score.annotation.Payable;
import score.annotation.EventLog;

import java.math.BigInteger;

/*
 * This contract allows admins to add claimable ICX amounts for users.
 */
public class Contract
{
    // Mapping of user addresses to their claimable ICX amounts
    private static final String CLAIMS = "claims";
    private static final String ADMINS = "admins";
    private static final String ICX_TOKEN = "ICX_TOKEN";
    private static final String BALN_TOKEN = "BALN_TOKEN";
    private final VarDB<Address> balnContract = Context.newVarDB("balnContract", Address.class);
    private static final BranchDB<String, DictDB<Address, BigInteger>> claims = Context.newBranchDB(CLAIMS, BigInteger.class);
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
     * Set the BALN contract address.
     */
    @External
    public void setBALNContract(Address balnContractAddress) {
        // Ensure only admins can add claims
        Contract contractInstance = new Contract();
        onlyAdmins(contractInstance);

        // Set the BALN contract address
        balnContract.set(balnContractAddress);
    }

    /*
     * Get the BALN contract address.
     */
    @External(readonly=true)
    public Address getBALNContract() {
        return balnContract.get();
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
    public String isAdmin(Address account) {
        return String.valueOf(admins.getOrDefault(account, false));
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

    public void _addClaim(Address user, BigInteger amount, String token) {
        // Ensure only admins can add claims
        Contract contractInstance = new Contract();
        onlyAdmins(contractInstance);

        // Update the claimable amount for the user
        claims.at(token).set(user, amount);
        ClaimAdded(user, amount, token);
    }

    /*
     * Add a claimable ICX amount for a user.
     */
    @External
    public void addICXClaim(Address user, BigInteger amount) {
        _addClaim(user, amount, ICX_TOKEN);
    }

    /*
     * Add a claimable BALN amount for a user.
     */
    @External
    public void addBALNClaim(Address user, BigInteger amount) {
        _addClaim(user, amount, BALN_TOKEN);
    }

    @External
    public void claimBALN() {
        Address caller = Context.getCaller();
        BigInteger claimAmount = claims.at(BALN_TOKEN).getOrDefault(caller, BigInteger.ZERO);

        // Ensure there is something to claim
        if (claimAmount.equals(BigInteger.ZERO)) {
            Context.revert("No BALN to claim");
        }

        // Clear the claimable amount for the user
        claims.at(BALN_TOKEN).set(caller, BigInteger.ZERO);

        // Transfer BALN token to caller
        sendBalnToken(caller, claimAmount);

        // Emit the Claimed event
        Claimed(caller, claimAmount, BALN_TOKEN);
    }

    @External
    public void claimICX() {
        Address caller = Context.getCaller();
        BigInteger claimAmount = claims.at(ICX_TOKEN).getOrDefault(caller, BigInteger.ZERO);

        // Ensure there is something to claim
        if (claimAmount.equals(BigInteger.ZERO)) {
            Context.revert("No ICX to claim");
        }

        // Clear the claimable amount for the caller
        claims.at(ICX_TOKEN).set(caller, BigInteger.ZERO);

        // Transfer ICX to the caller
        Context.transfer(caller, claimAmount);

        // Emit the Claimed event
        Claimed(caller, claimAmount, ICX_TOKEN);
    }

    /*
     * Claim the ICX amount by an admin.
     */
    @External
    public void adminClaimICX(BigInteger amount) {
        // Ensure only admins can claim
        Contract contractInstance = new Contract();
        onlyAdmins(contractInstance);

        Address caller = Context.getCaller();
        Address owner = Context.getOwner();

        // Transfer ICX to the user
        Context.transfer(owner, amount);

        // Emit the Claimed event
        OwnerClaimed(caller, amount, ICX_TOKEN);
    }

    /*
     * Claim BALN amount by an admin.
     */
    @External
    public void adminClaimBALN(BigInteger amount) {
        // Ensure only admins can claim
        Contract contractInstance = new Contract();
        onlyAdmins(contractInstance);

        Address caller = Context.getCaller();
        Address owner = Context.getOwner();

        // Transfer BALN to contract owner
        sendBalnToken(owner, amount);

        // Emit the Claimed event
        OwnerClaimed(caller, amount, BALN_TOKEN);
    }

    public static void onlyAdmins(Contract contractInstance) {

        Address caller = Context.getCaller();
        Context.require(contractInstance.admins.getOrDefault(caller, false), "Only admins can execute this function");
    }

    /*
     * Return the claimable ICX amount for a user.
     */
    @External(readonly=true)
    public BigInteger getICXClaimableAmount(Address user) {
        return claims.at(ICX_TOKEN).getOrDefault(user, BigInteger.ZERO);
    }

    /*
     * Return the claimable BALN amount for a user.
     */
    @External(readonly=true)
    public BigInteger getBALNClaimableAmount(Address user) {
        return claims.at(BALN_TOKEN).getOrDefault(user, BigInteger.ZERO);
    }


    /*
     * Send BALN token to the user.
     */
    private void sendBalnToken(Address to, BigInteger amount) {
        Address balnContractAddress = balnContract.getOrDefault(null);

        Context.require(balnContractAddress != null, "BALN contract address is not set");

        // Transfer BALN token to the user
        Context.call(balnContractAddress, "transfer", to, amount);
    }

    /*
     * Fallback function to allow the contract to receive ICX.
     */
    @Payable
    public void fallback() {
    }

    /*
     * allows contract to receive tokens
     */
    @External
    public void tokenFallback(Address _from, BigInteger _value, byte[] _data) {
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
    @EventLog(indexed=3)
    public void ClaimAdded(Address user, BigInteger amount, String token) {}

    /*
     * Event emitted when a claim is made by a user.
     */
    @EventLog(indexed=3)
    public void Claimed(Address user, BigInteger amount, String token) {}

    /*
     * Event emitted when a claim is made by an admin.
     */
    @EventLog(indexed=3)
    public void OwnerClaimed(Address owner, BigInteger amount, String token) {}
}
