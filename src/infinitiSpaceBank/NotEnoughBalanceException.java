package infinitiSpaceBank;

/**
 * The exception to throw when there is not enough balance when a user want to withdraw or transfer money
 * @author Yannong Li
 */
public class NotEnoughBalanceException extends Exception {

    public NotEnoughBalanceException(String message) {
        super(message);
    }
}
