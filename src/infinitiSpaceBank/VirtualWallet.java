package infinitiSpaceBank;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a virtual wallet that is created by a user and manages all accounts of this user
 * @author Yannong Li
 */
public class VirtualWallet {

    //List of all accounts of the owner of this wallet
    private List<Account> accounts;
    //Number of accounts in this wallet
    private int size;

    /**
     * Class constructor
     */
    public VirtualWallet(){
        accounts = new ArrayList<>();
        size = 0;
    }

    /**
     * Create a new account in this Virtual Wallet and add it to the wallet
     */
    public void createAccount(){
        Account newAccount = new Account(size);
        accounts.add(newAccount);
        size++;
    }

    /**
     * Create a new account with a specific amount of initial balance in this Virtual Wallet and add it to the wallet
     * @param amount the initial balance of this new account
     */
    public void createAccount(double amount){
        Account newAccount = new Account(size, amount);
        accounts.add(newAccount);
        size++;
    }

    /**
     * Get all accounts
     * @return the list of all accounts in this wallet
     */
    public List<Account> getAllAccounts() {
        if(size() == 0)
            throw new NullPointerException("There is no account available");
        return accounts;
    }

    /**
     * Get a specific account
     * @param index the account number that you want to get
     * @return the specific account you want
     */
    public Account getAccount(int index) {
        if(size() == 0)
            throw new NullPointerException("There is no account available");
        if(index < 0 || index >= size())
            throw new IndexOutOfBoundsException("This account doesn't exist");
        return accounts.get(index);
    }

    /**
     * Get the number of accounts
     * @return the number of accounts in this wallet
     */
    public int size(){
        return size;
    }
}
