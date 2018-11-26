package infinitiSpaceBank;

/**
 * Represents a user that can create exactly one virtual wallet and multiple accounts
 * @author Yannong Li
 */
public class User {

    private VirtualWallet wallet;
    private String userName;

    /**
     * Class constructor with a username
     * @param userName the username of this user
     */
    public User(String userName){
        if(userName == null || userName.length() == 0)
            throw new IllegalArgumentException("Please enter a valid userName.");
        this.userName = userName;
    }

    /**
     * Create a Virtual Wallet owned by this user
     */
    public void createWallet(){
        wallet = new VirtualWallet();
    }

    /**
     * Get the Virtual Wallet
     * @return the virtual wallet of this user
     */
    public VirtualWallet getWallet() {
        if(wallet == null)
            throw new NullPointerException("Please create a wallet first.");
        return wallet;
    }

    /**
     * Get the username
     * @return the username of this user
     */
    public String getUserName() {
        return userName;
    }
}
