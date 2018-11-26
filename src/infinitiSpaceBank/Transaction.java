package infinitiSpaceBank;


import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

/**
 * Represents a transaction record with a globally unique ID, transaction amount and other attributes
 * Since this class consists of only setters and getters, naive unit test is ignored
 */
public class Transaction {
    //Destination account is null in deposit and withdraw method
    private Account account, destAccount;
    private String transID;
    private String transType;
    private Timestamp time;
    private double transAmount;

    public Transaction(String transType, double transAmount, Account account) {
        UUID uuid = UUID.randomUUID();
        time = new Timestamp(new Date().getTime());

        this.transID = uuid.toString();
        this.transType = transType;
        this.transAmount = transAmount;
        this.account = account;
        this.destAccount = null;

    }

    public void setDestAccount(Account destAccount){
        this.destAccount = destAccount;
    }

    public String getDestAccount(){
        return "Account" + destAccount.getAccountNumber();
    }

    public String getTransID() {
        return transID;
    }

    public String getTransType() {
        return transType;
    }

    public Timestamp getTime() {
        return time;
    }

    public double getTransAmount() {
        return transAmount;
    }

    @Override
    public String toString(){
        if(destAccount == null)
            return getTransID() + " " + getTransType() + " " + getTransAmount() + " at " + getTime();
        else
            return getTransID() + " " + getTransType() + " " + getTransAmount() + " to " + getDestAccount()+
                    " at " + getTime();
    }
}
