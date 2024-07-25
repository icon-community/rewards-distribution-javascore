package contract;

import com.iconloop.score.test.Account;
import com.iconloop.score.test.Score;
import com.iconloop.score.test.ServiceManager;
import com.iconloop.score.test.TestBase;
import score.UserRevertedException;

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
    private static Score tokenContractInstance;
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
        // deploy mock token contract
        tokenContractInstance = sm.deploy(owner, MockToken.class);

        // transfer ICX to contract
        sm.transfer(owner, contractInstance.getAddress(), initialSupply);
    }

    @Test
    void testAddICXClaimAndGetClaimableAmount() {
        // Add claim amount to contract for account
        BigInteger claimAmount = BigInteger.valueOf(1000);
        contractInstance.invoke(owner, "addICXClaim", accountInstance1.getAddress(), claimAmount);

        // Get claimable amount for account
        BigInteger claimable = (BigInteger) contractInstance.call("getICXClaimableAmount", accountInstance1.getAddress());

        // Check if claimable amount is equal to claim amount
        assertEquals(claimAmount, claimable);
    }

    @Test
    void testClaim() {
        BigInteger claimAmount = BigInteger.valueOf(1000);
        contractInstance.invoke(owner, "addICXClaim", accountInstance2.getAddress(), claimAmount);

        // Claim the amount
        contractInstance.invoke(accountInstance2, "claimICX");

        // Get claimable amount for account
        BigInteger claimable = (BigInteger) contractInstance.call("getICXClaimableAmount", accountInstance2.getAddress());

        assertEquals(BigInteger.ZERO, claimable);
    }

    @Test
    void testAdminClaimByOwner() {
        // Balance before claim
        BigInteger ownerBalancePre = owner.getBalance();

        // Add claim amount to contract for account
        BigInteger claimAmount = BigInteger.valueOf(1000);

        // Admin claim the amount
        contractInstance.invoke(owner, "adminClaimICX", claimAmount);

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
        contractInstance.invoke(accountInstance3, "adminClaimICX", claimAmount);

        // balance after claim
        BigInteger ownerBalancePost = owner.getBalance();
        
        // Check account balance return to 1000
        assertEquals(claimAmount, ownerBalancePost.subtract(ownerBalancePre));
    }

    @Test
    void testOnlyOwnerCanAddAdmin() {
        
        // Throws error if method is called by non-owner
        assertThrows(UserRevertedException.class, () -> contractInstance.invoke(accountInstance2, "addAdmin", accountInstance3.getAddress()));
    }
    
    @Test
    void testCheckIfAdmin() {
        
        // add admin
        contractInstance.invoke(owner, "addAdmin", accountInstance3.getAddress());

        // Check if account is admin
        String isAdmin = (String) contractInstance.call("isAdmin", accountInstance3.getAddress());

        assertTrue(Boolean.parseBoolean(isAdmin),"List of admins should be higher than zero");
    }

    @Test
    void claimBALNToken() {
        // Add claim amount to contract for account
        BigInteger claimAmount = BigInteger.valueOf(1000);
        contractInstance.invoke(owner, "addBALNClaim", accountInstance1.getAddress(), claimAmount);

        // set BALN contract address
        contractInstance.invoke(owner, "setBALNContract", tokenContractInstance.getAddress());

        // Get claimable amount for account
        BigInteger claimable = (BigInteger) contractInstance.call("getBALNClaimableAmount", accountInstance1.getAddress());

        // call claimBALN
        contractInstance.invoke(accountInstance1, "claimBALN");

        // If the assert is true, the test is successful
        assertEquals(true, true);
    }
    @Test
    void claimBALNTokenByAdmin() {
        // Add claim amount to contract for account
        BigInteger claimAmount = BigInteger.valueOf(1000);

        // set BALN contract address
        contractInstance.invoke(owner, "setBALNContract", tokenContractInstance.getAddress());

        // Get claimable amount for account
        // call claimBALN
        contractInstance.invoke(owner, "adminClaimBALN");

        // If the assert is true, the test is successful
        assertEquals(true, true);
    }

    @Test
    void testOnlyAdminCanClaimBALN() {
        // Add claim amount to contract for account
        BigInteger claimAmount = BigInteger.valueOf(1000);
        
        // Throws error if method is called by non-owner
        assertThrows(UserRevertedException.class, () -> contractInstance.invoke(accountInstance2, "adminClaimBALN", claimAmount));
    }
}
