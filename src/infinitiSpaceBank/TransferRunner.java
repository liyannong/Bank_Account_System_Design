package infinitiSpaceBank;

import java.util.concurrent.CountDownLatch;

/**
 * A transfer runner for testing multi-threading in AccountTest
 */
public class TransferRunner implements Runnable {
    private Account srcAccount, destAccount;
    private double amount;
    private CountDownLatch latch;

    TransferRunner(Account srcAccount, Account destAccount, CountDownLatch latch, double amount) {
        this.srcAccount = srcAccount;
        this.destAccount = destAccount;
        this.amount = amount;
        this.latch = latch;
    }

    public void run() {
        try {
            while (true){
                if(srcAccount.transfer(destAccount, amount)){
                    latch.countDown();
                    return;
                }else{
                    continue;
                }
            }
        }catch(NotEnoughBalanceException e){

        }
        System.out.printf("%s completed\n", Thread.currentThread().getName());
    }
}