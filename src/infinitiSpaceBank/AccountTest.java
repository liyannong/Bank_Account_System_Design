package infinitiSpaceBank;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.*;

/**
 * Junit test for Account class. This test class contributes the most in all test classes.
 * All tests pass through over 1000 times
 */
public class AccountTest {
    private Account account1, account2, account3, account4;

    @Before
    public void setUp() throws Exception {
        account1 = new Account(0);
        account2 = new Account(1, 100.0);
        account3 = new Account(2, 200.0);
        account4 = new Account(3, 300.0);
    }

    @Test
    public void testGetAccountNumber() {
        assertEquals(1, account1.getAccountNumber());
        assertEquals(2, account2.getAccountNumber());
        assertEquals(3, account3.getAccountNumber());
    }

    @Test
    public void testGetBalance() {
        assertEquals(0.0, account1.getBalance());
        assertEquals(100.0, account2.getBalance());
        assertEquals(200.0, account3.getBalance());
    }

    @Test
    public void testWithdraw(){
        try{
            account1.withdraw(50.0);
            fail("Check if balance is enough");
        }catch(NotEnoughBalanceException e){

        }

        try{
            assertTrue(account2.withdraw(50.0));
            assertTrue(account3.withdraw(50.0));
            assertEquals(50.0, account2.getBalance());
            assertEquals(150.0, account3.getBalance());
        }catch(NotEnoughBalanceException e){

        }
    }

    @Test
    public void testDeposit() {
        assertTrue(account1.deposit(50.0));
        assertTrue(account2.deposit(50.0));
        assertTrue(account3.deposit(50.0));
        assertEquals(50.0, account1.getBalance());
        assertEquals(150.0, account2.getBalance());
        assertEquals(250.0, account3.getBalance());

    }

    @Test
    public void testTransfer() {
        try{
            account1.transfer(account2, 50.0);
            fail("Check if balance is enough");
        }catch(NotEnoughBalanceException e){

        }

        try{
            assertTrue(account3.transfer(account2, 50.0));
            assertEquals(150.0, account2.getBalance());
            assertEquals(150.0, account3.getBalance());
        }catch(NotEnoughBalanceException e){

        }

    }

    @Test
    public void testGetLastNTransaction(){
        try{
            account1.deposit(300);
            account1.withdraw(50);
            account2.deposit(300);
            account2.withdraw(50);

            account1.transfer(account2, 100);
            account2.transfer(account1, 200);


            String[] acc1Trans = account1.getLastNTransaction(4);
            String[] acc2Trans = account2.getLastNTransaction(3);

            //Test if transaction types are correct
            //Other attributes like transTime, transAmount can also be checked using the same logic
            assertEquals("deposit", acc1Trans[0].split(" ")[1]);
            assertEquals("transfer", acc1Trans[1].split(" ")[1]);

            assertEquals("transfer", acc2Trans[0].split(" ")[1]);
            assertEquals("withdraw", acc2Trans[2].split(" ")[1]);
        }catch (NotEnoughBalanceException e){
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void testConcurrentDeposit(){
        try{
            CountDownLatch latch = new CountDownLatch(10);
            Runnable depositRunner = new Runnable(){
                    @Override
                    public void run() {
                        while(true){
                            if(account1.deposit(50.0)){
                                latch.countDown();
                                break;
                            }
                        }
                    }
                } ;

            for(int i = 0; i < 10; i++){
               new Thread(depositRunner, "thread " + i).start();
            }

            latch.await();

            assertEquals(500.0, account1.getBalance());
        }catch(InterruptedException e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testConcurrentWithdraw(){
        try{
            CountDownLatch latch2 = new CountDownLatch(5);
            Runnable withdrawRunner = new Runnable(){
                @Override
                public void run() {
                    while(true){
                        try{
                            if(account4.withdraw(50.0)){
                                latch2.countDown();
                                return;
                            }
                        }catch(NotEnoughBalanceException e){
                            System.out.println("Not enough balance!");
                        }
                    }
                }
            };

            for(int i = 0; i < 5; i++){
                new Thread(withdrawRunner, "thread " + i).start();
            }

            latch2.await();
            assertEquals(50.0, account4.getBalance());
        }catch(InterruptedException e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testConcurrentTransfer(){
        try{
            CountDownLatch depositLatch = new CountDownLatch(1);
            account1.deposit(100);
            depositLatch.countDown();

            CountDownLatch latch3 = new CountDownLatch(4);

            new Thread(new TransferRunner(account1, account2, latch3, 50 ), "thread " + 0).start();
            new Thread(new TransferRunner(account2, account1, latch3, 50 ), "thread " + 1).start();
            new Thread(new TransferRunner(account3, account4, latch3, 50 ), "thread " + 2).start();
            new Thread(new TransferRunner(account4, account3, latch3, 50 ), "thread " + 3).start();

            latch3.await();
            assertEquals(100.0, account1.getBalance());
            assertEquals(100.0, account2.getBalance());
            assertEquals(200.0, account3.getBalance());
            assertEquals(300.0, account4.getBalance());
        }catch(InterruptedException e){
            System.out.println(e.getMessage());
        }
    }
}

