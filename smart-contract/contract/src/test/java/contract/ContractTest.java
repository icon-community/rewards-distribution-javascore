package contract;

import com.iconloop.score.test.Account;
import com.iconloop.score.test.Score;
import com.iconloop.score.test.ServiceManager;
import com.iconloop.score.test.TestBase;

import score.UserRevertedException;
import score.Address;

import scorex.util.ArrayList;

import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContractTest extends TestBase {
    private static final ServiceManager sm = getServiceManager();
    private static final Account owner = sm.createAccount();
    private static Score contractInstance;
    private static Account accountInstance1;
    private static Account accountInstance2;
    private static Account accountInstance3;



    @BeforeAll
    public static void setup() throws Exception {

        BigInteger initialSupply = BigInteger.valueOf(3000);
        // add balance to owner account
        owner.addBalance(initialSupply);

        BigInteger ownerBalance = owner.getBalance();
        // System.out.println("owner balance: " + ownerBalance);

        // create user account
        accountInstance1 = sm.createAccount();
        accountInstance2 = sm.createAccount();
        accountInstance3 = sm.createAccount();

        // deploy contract
        contractInstance = sm.deploy(owner, Contract.class);
        // transfer ICX to contract
        sm.transfer(owner, contractInstance.getAddress(), initialSupply);
    }

    @Test
    void testAddClaimAndGetClaimableAmount() {
        // Add claim amount to contract for account
        BigInteger claimAmount = BigInteger.valueOf(1000);
        contractInstance.invoke(owner, "addClaim", accountInstance1.getAddress(), claimAmount);

        // Get claimable amount for account
        BigInteger claimable = (BigInteger) contractInstance.call("getClaimableAmount", accountInstance1.getAddress());

        // Check if claimable amount is equal to claim amount
        assertEquals(claimAmount, claimable);
    }

    @Test
    void testClaim() {
        BigInteger claimAmount = BigInteger.valueOf(1000);
        contractInstance.invoke(owner, "addClaim", accountInstance2.getAddress(), claimAmount);

        // Claim the amount
        contractInstance.invoke(accountInstance2, "claim");

        // Get claimable amount for account
        BigInteger claimable = (BigInteger) contractInstance.call("getClaimableAmount", accountInstance2.getAddress());

        assertEquals(BigInteger.ZERO, claimable);
    }

    @Test
    void testAdminClaimByOwner() {
        // Balance before claim
        BigInteger ownerBalancePre = owner.getBalance();

        // Add claim amount to contract for account
        BigInteger claimAmount = BigInteger.valueOf(1000);

        // Admin claim the amount
        contractInstance.invoke(owner, "adminClaim", claimAmount);

        BigInteger ownerBalance = owner.getBalance();

        // balance after claim
        BigInteger ownerBalancePost = owner.getBalance();
        
        // Check account balance return to 1000
        assertEquals(claimAmount, ownerBalancePost.subtract(ownerBalancePre));
    }

    @Test
    void testAdminClaimByNewAdmin() {

        // Balance before claim
        BigInteger ownerBalancePre = owner.getBalance();

        // Add claim amount to contract for account
        BigInteger claimAmount = BigInteger.valueOf(1000);
        
        // create new admin account
        contractInstance.invoke(owner, "addAdmin", accountInstance3.getAddress());

        // Admin claim the amount
        contractInstance.invoke(accountInstance3, "adminClaim", claimAmount);

        // balance after claim
        BigInteger ownerBalancePost = owner.getBalance();
        
        // Check account balance return to 1000
        assertEquals(claimAmount, ownerBalancePost.subtract(ownerBalancePre));
    }
    
    @Test
    void testGetAdmins() {
        
        List admins = (List) contractInstance.call("getAdmins");
        int size = admins.size();
        // System.out.println("admins size: " + size);
        // System.out.println("admins: "+ admins);

        assertTrue(size > 0,"List of admins should be higher than zero");
    }
}
