package contract;

import com.iconloop.score.test.Account;
import com.iconloop.score.test.Score;
import com.iconloop.score.test.ServiceManager;
import com.iconloop.score.test.TestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import score.UserRevertedException;
import scorex.util.ArrayList;
import java.math.BigInteger;
import score.Address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContractTest extends TestBase {
    private static final ServiceManager sm = getServiceManager();
    private static final Account owner = sm.createAccount();
    private static Score contractInstance;
    private static Account accountInstance;



    @BeforeAll
    public static void setup() throws Exception {
        // add balance to owner account
        owner.addBalance(BigInteger.valueOf(3000));

        BigInteger ownerBalance = owner.getBalance();
        System.out.println("owner balance: " + ownerBalance);

        // deploy contract
        contractInstance = sm.deploy(owner, Contract.class);
        Address contractAddress = contractInstance.getAddress();
        // create account
        accountInstance = sm.createAccount();

        // transfer ICX to contract
        // sm.transfer(owner, contractAddress, BigInteger.valueOf(2000));
        // sm.transfer(owner, accountInstance.getAddress(), BigInteger.valueOf(2000));
        // BigInteger accountBalance = accountInstance.getBalance();
        // System.out.println("account balance: " + accountBalance);
    }

    @Test
    void testAddClaimAndGetClaimableAmount() {
        // Add claim amount to contract for account
        BigInteger claimAmount = BigInteger.valueOf(1000);
        contractInstance.invoke(owner, "addClaim", accountInstance.getAddress(), claimAmount);

        // Get claimable amount for account
        BigInteger claimable = (BigInteger) contractInstance.call("getClaimableAmount", accountInstance.getAddress());

        // Check if claimable amount is equal to claim amount
        assertEquals(claimAmount, claimable);
    }

    // @Test
    // void testClaim() {
    //     // Add claim amount to contract for account
    //     BigInteger claimAmount = BigInteger.valueOf(1000);
    //     contractInstance.invoke(owner, "addClaim", accountInstance.getAddress(), claimAmount);

    //     // Claim the amount
    //     contractInstance.invoke(accountInstance, "claim");

    //     // Get claimable amount for account
    //     BigInteger claimable = (BigInteger) contractInstance.call("getClaimableAmount", accountInstance.getAddress());

    //     // Check if claimable amount is equal to 0
    //     assertEquals(BigInteger.ZERO, claimable);
    // }
}
