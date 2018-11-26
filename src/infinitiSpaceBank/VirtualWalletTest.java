package infinitiSpaceBank;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

/**
 * Junit test for VirtualWallet class
 */
public class VirtualWalletTest {
    VirtualWallet wallet;

    @Before
    public void setup(){
        wallet = new VirtualWallet();
    }

    @Test
    public void testCreateAndGetAccount(){
        try {
            assertNotNull(wallet.getAccount(-1));
            fail("Check account existence");
        }catch(NullPointerException e){

        }

        wallet.createAccount();
        assertNotNull(wallet.getAccount(0));
        wallet.createAccount(100.0);
        assertNotNull(wallet.getAccount(0));

        try {
            assertNotNull(wallet.getAccount(-1));
            fail("Check out of bounds");
        }catch(IndexOutOfBoundsException e){

        }

        try {
            assertNotNull(wallet.getAccount(5));
            fail("Check out of bounds");
        }catch(IndexOutOfBoundsException e){

        }

        assertEquals(0.0, wallet.getAccount(0).getBalance());
        assertEquals(100.0, wallet.getAccount(1).getBalance());
    }

    @Test
    public void testSize() {
        wallet.createAccount();
        assertEquals(1, wallet.size());
        wallet.createAccount();
        assertEquals(2, wallet.size());
    }
}