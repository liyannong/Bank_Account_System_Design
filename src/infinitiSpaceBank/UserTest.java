package infinitiSpaceBank;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Junit test for User class
 */
public class UserTest {
    User user1, user2;

    @Before
    public void setup() {
        user1 = new User("Yannong");
        user2 = new User("HR");

    }

    @Test
    public void testCreateAndGetWallet() {
        user1.createWallet();
        user2.createWallet();
        assertNotNull(user1.getWallet());
        assertNotNull(user2.getWallet());
    }

    @Test
    public void testGetUserName() {
        assertEquals("Yannong", user1.getUserName());
        assertEquals("HR", user2.getUserName());
    }
}