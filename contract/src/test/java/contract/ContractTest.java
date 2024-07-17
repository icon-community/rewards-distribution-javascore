package contract;

import com.iconloop.score.test.Account;
import com.iconloop.score.test.Score;
import com.iconloop.score.test.ServiceManager;
import com.iconloop.score.test.TestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import score.UserRevertedException;
import scorex.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContractTest extends TestBase {
    private static final ServiceManager sm = getServiceManager();
    private static final Account owner = sm.createAccount();
    private static final String trueString = "true";

    private static Score contractInstance;
    private static Account accountInstance;

    @BeforeAll
    public static void setup() throws Exception {
        contractInstance = sm.deploy(owner, Contract.class);
        accountInstance = sm.createAccount();
        // registrationBookScore.invoke(luffy, "registerUser", luffy.getAddress());
    }

    @Test
    void getRegistrationBookSize() {
        assertEquals('a', 'a');
    }
}
