package contract;

import score.Address;
import score.Context;
import score.ArrayDB;
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
    private static final ArrayDB<Address> admins = Context.newArrayDB(ADMINS, Address.class);

    /*
     * Constructor that adds the contract owner as an admin.
     */
    public Contract() {
        // Add the contract owner as an admin
        Address ownerAddress = Context.getOwner();
        int size = admins.size();
        for (int i = 0; i < size; i++) {
            String adminAddress = admins.get(i).toString();
            if (adminAddress.equals(ownerAddress.toString())) {
                return;
            }
        }
        admins.add(Context.getOwner());
        AdminAdded(Context.getOwner());
    }

    /*
     * Add an admin to the contract.
     */
    @External
    public void addAdmin(Address admin) {
        // Ensure only the owner can add admins
        Address caller = Context.getCaller();

        if (caller.equals(Context.getOwner())) {
            // check if the admin is already added
            int size = admins.size();
            for (int i = 0; i < size; i++) {
                String adminAddress = admins.get(i).toString();
                if (adminAddress.equals(admin.toString())) {
                    Context.revert("Admin already added");
                }
            }
            // Add the admin
            admins.add(admin);
            AdminAdded(admin);
        } else {
            Context.revert("Only the contract owner can add admins");
        }
    }

    /*
     * Return the list of admins.
     */
    @External(readonly=true)
    public List<Address> getAdmins() {
        int size = admins.size();
        Address [] adminArray = new Address[size];
        for (int i = 0; i < size; i++) {
            adminArray[i] = admins.get(i);
        }
        return List.of(adminArray);
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
        claims.set(user, amount);
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
        Boolean isAdmin = false;
        int size = contractInstance.admins.size();
        for (int i = 0; i < size; i++) {
            if (caller.equals(contractInstance.admins.get(i))) {
                isAdmin = true;
                break;
            }
        }

        if (isAdmin == false) {
            Context.revert("Only contract admins can execute this function");
        }
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
