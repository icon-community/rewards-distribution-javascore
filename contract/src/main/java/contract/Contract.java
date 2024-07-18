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

public class Contract
{
    // Mapping of user addresses to their claimable ICX amounts
    private static final String CLAIMS = "claims";
    private static final String ADMINS = "admins";
    private static final IterableDictDB<Address, BigInteger> claims = new IterableDictDB<>(CLAIMS, BigInteger.class, Address.class, false);
    private final ArrayDB<Address> admins = Context.newArrayDB(ADMINS, Address.class);

    public Contract() {
        // Add the contract owner as an admin
        this.admins.add(Context.getOwner());
        AdminAdded(Context.getOwner());
    }

    // @External
    // public void addAdmin(Address admin) {
    //     // Ensure only the owner can add admins
    //     Address caller = Context.getCaller();
    //     if (caller.equals(Context.getOwner())) {
    //     // Add the admin
    //       this.admins.add(admin);
    //       AdminAdded(admin);
    //     } else {
    //         Context.revert("Only the contract owner can add admins");
    //     }

    // }

    @External(readonly=true)
    public List<Address> getAdmins() {
        int size = this.admins.size();
        System.out.println("Size: " + size);
        Address [] adminArray = new Address[size];
        for (int i = 0; i < size; i++) {
            System.out.println("i: " + i);
            System.out.println("Admin: " + this.admins.get(i));
            adminArray[i] = this.admins.get(i);
        }
        return List.of(adminArray);
    }

    // Add claimable ICX for a user
    @External
    public void addClaim(Address user, BigInteger amount) {
        // Ensure only admins can add claims
        // Contract contractInstance = new Contract();
        // onlyAdmins(contractInstance);

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
    public void adminClaim(BigInteger amount) {
        // Ensure only admins can claim ICX
        // Contract contractInstance = new Contract();
        // onlyAdmins(contractInstance);

        Address caller = Context.getCaller();

        // Transfer ICX to the user
        Context.transfer(caller, amount);

        // Emit the Claimed event
        OwnerClaimed(caller, amount);
    }

    // public static void onlyAdmins(Contract contractInstance) {
    //     Address caller = Context.getCaller();
    //     Boolean isAdmin = false;
    //     int size = contractInstance.admins.size();
    //     for (int i = 0; i < size; i++) {
    //         if (caller.equals(contractInstance.admins.get(i))) {
    //             isAdmin = true;
    //             break;
    //         }
    //     }

    //     if (isAdmin == false) {
    //         Context.revert("Only contract admins can execute this function");
    //     }
    // }

    // Get the claimable ICX amount for a user
    @External(readonly=true)
    public BigInteger getClaimableAmount(Address user) {
        return claims.getOrDefault(user, BigInteger.ZERO);
    }

    @Payable
    public void fallback() {
    }

    @EventLog(indexed=2)
    public void AdminAdded(Address admin) {}

    @EventLog(indexed=2)
    public void ClaimAdded(Address user, BigInteger amount) {}

    @EventLog(indexed=2)
    public void Claimed(Address user, BigInteger amount) {}

    @EventLog(indexed=2)
    public void OwnerClaimed(Address owner, BigInteger amount) {}
}
