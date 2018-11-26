package infinitiSpaceBank;

import java.sql.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents an account that can perform withdraw, deposit and transfer transaction
 * @author Yannong Li
 */
public class Account {

    private int accountNumber;
    private double balance;

    //Lock object that handles concurrency of each account
    private final Lock lock = new ReentrantLock();
    //Table name for each account in the database
    private String tableName;
    //Boolean flag to skip the statement in deposit and withdraw if called from transfer
    //Set up in transfer and used in deposit & withdraw
    private boolean skip;

    /**
     * Class constructor with 0.0 initial balance
     * @param accountNumber the accountNumber of this account
     */
    public Account(int accountNumber) {
        this.accountNumber = accountNumber;
        balance = 0.0;
        skip = false;

        //You need to give each account a new Connection, or thread safety will be ruined
        try(Connection conn = this.connect();
            Statement stmt = conn.createStatement()){

            tableName = "Account" + getAccountNumber() + "Transaction";
            stmt.execute("DROP TABLE IF EXISTS " + tableName);
            stmt.execute("CREATE TABLE IF NOT EXISTS " + tableName +
                    " (transID VARCHAR(100), transType VARCHAR(40), transAmount DOUBLE, transTime VARCHAR(40), destAccount VARCHAR(40))");

        }catch(SQLException e){
            System.out.println(e.getMessage());
            System.out.println("Connection failed!");
        }
     }



    /**
     * Class constructor with a specific initial balance
     * @param accountNumber the accountNumber of this account
     * @param balance       the specific initial balance
     */
    public Account(int accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        skip = false;

        //You need to give each account a new Connection, or thread safety will be ruined
        try(Connection conn = this.connect();
            Statement stmt = conn.createStatement()){

            tableName = "Account" + getAccountNumber() + "Transaction";
            stmt.execute("DROP TABLE IF EXISTS " + tableName);
            stmt.execute("CREATE TABLE IF NOT EXISTS " + tableName +
                    " (transID VARCHAR(100), transType VARCHAR(40), transAmount DOUBLE, transTime VARCHAR(40), destAccount VARCHAR(40))");

        }catch(SQLException e){
            System.out.println(e.getMessage());
            System.out.println("Connection failed!");
        }
    }

    /**
     * Set up the connection using JDBC and SQLite. In following methods, we utilize the try with resources function of Java.
     * So we don't need bother to close it in the end.
     * @return the Connection with SQLite databases
     */
    private Connection connect() {
        // For each Account, there is one database established
        // In each database, there is one Table of Transaction Records
        String dbName = "Account" + getAccountNumber() + "DB";
        Connection conn = null;
        try {
            //This project is created using IntelliJ IDEA
            //To reproduce the project, one needs to first download SQLite and add the .jar file to Project Library
            conn = DriverManager.getConnection("jdbc:sqlite:D:\\databases\\" + dbName + ".db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * Get the accountNumber
     * @return the accountNumber
     */
    public int getAccountNumber() {
        return accountNumber+1;
    }

    /**
     * Get current balance
     * @return a double representing the balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Deposit a specific amount of money to the account
     * @param amount the amount to deposit
     * @return true  if deposit succeeds;
     *         false if deposit fails;
     */
    public boolean deposit(double amount) {
        if (lock.tryLock()) {
            try(Connection conn = this.connect();
                Statement stmt = conn.createStatement()){
                //Perform deposit action
                balance += amount;
                System.out.println("Account " + getAccountNumber() + " current balance: " + getBalance());

                //Create transaction record and write to the database
                Transaction trans = new Transaction("deposit", amount, this);
                tableName = "Account" + this.getAccountNumber() + "Transaction";
                if(!skip)
                    stmt.execute("INSERT INTO " + tableName +" VALUES('"+ trans.getTransID() + "','" +
                            trans.getTransType() + "','" + amount + "','" + trans.getTime() + "'," +
                            "NULL" + ")");
                return true;
            } catch(SQLException e){
                System.out.println(e.getMessage());
            }finally {
                lock.unlock();
            }
        }
        return false;

    }

    /**
     * Withdraw a specific amount of money from the account
     * @param amount the amount to withdraw
     * @return true  if withdrawal succeed;
     *         false if withdrawal fails;
     * @throws NotEnoughBalanceException if there is not enough balance to withdraw
     */
    public boolean withdraw(double amount) throws NotEnoughBalanceException {
        if (lock.tryLock()) {
            try(Connection conn = this.connect();
                Statement stmt = conn.createStatement()) {
                if (amount > balance)
                    throw new NotEnoughBalanceException("There is not enough balance!");
                else {
                    //Perform withdraw action
                    System.out.println(Thread.currentThread().getName() + " is withdrawing " + amount);
                    balance -= amount;
                    System.out.println("Withdraw successful. Account " + getAccountNumber() +
                            " balance now is: " + getBalance());

                    //Create transaction record and write to the database
                    Transaction trans = new Transaction("withdraw", amount, this);
                    tableName = "Account" + this.getAccountNumber() + "Transaction";
                    if(!skip)
                        stmt.execute("INSERT INTO " + tableName +" VALUES('"+ trans.getTransID() + "','" +
                            trans.getTransType() + "','" + amount + "','" + trans.getTime() + "'," +
                            "NULL" + ")");
                    return true;
                }
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }finally {
                lock.unlock();
            }
        }
        return false;
    }

    /**
     * Transfer a specific amount of money to another user's first(default) account
     * @param destUser another user you transfer to
     * @param amount   the amount to transfer
     * @return true  if transfer succeeds;
     *		   false if transfer fails;
     * @throws NotEnoughBalanceException if there is not enough balance to transfer
     */
    public boolean transfer(User destUser, double amount) throws NotEnoughBalanceException{
        return transfer(destUser.getWallet().getAccount(0), amount);
    }

    /**
     * Transfer a specific amount of money to another account
     * @param destAccount another account you transfer to
     * @param amount      the amount to transfer
     * @return true  if transfer succeeds;
     *		   false if transfer fails;
     * @throws NotEnoughBalanceException if there is not enough balance to transfer
     */
    public boolean transfer(Account destAccount, double amount) throws NotEnoughBalanceException {
        /* The concurrency of transfer transaction is handled by withdraw and deposit methods
           Specifically, for a given account the Lock object can be acquired by only one thread
           If every step of transferring succeed, transfer succeed
           Else, money will be refunded back to the original account
        */

        //When transfer method calls withdraw and deposit, skip the SQL command in them.
        //This is to avoid the SQL commands holding the lock for too long
        skip = true;
        if (amount > balance)
            throw new NotEnoughBalanceException("There is not enough balance!");
        else {
            try(Connection conn = this.connect();
                Statement stmt = conn.createStatement()) {
                if (this.withdraw(amount)) {
                    if (destAccount.deposit(amount)) {
                        //If both true, transfer succeeds
                        String srcTableName = "Account" + this.getAccountNumber() + "Transaction";

                        //Create new transaction record and write to database
                        Transaction trans = new Transaction("transfer", amount, this);
                        trans.setDestAccount(destAccount);
                        stmt.execute("INSERT INTO " + srcTableName +" VALUES('" + trans.getTransID() + "','" +
                                trans.getTransType() + "','" + amount + "','" + trans.getTime() + "','" +
                                trans.getDestAccount() + "')");

                        skip = false;
                        return true;
                    } else {
                        System.out.println("Account " + destAccount.getAccountNumber() +
                                " is busy. Refunding money.");
                        //Deposit fails, deposit the money back to srcAccount
                        //Only exit when the deposit is successful, or the money will disappear
                        while (! this.deposit(amount))
                            continue;
                    }
                }
            }
            catch (SQLException e){
                System.out.println(e.getMessage());
            }
       }
        skip = false;
        return false;
    }


    /**
     * Retrieve and print out last N transaction records of this account
     * @param N the number of transaction records to show
     * @return string array of last N transaction records
     */
    public String[] getLastNTransaction(int N) {
        String[] res = new String[N];
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()){

            System.out.println("The last " + N + " transaction records of Account " + getAccountNumber() + ":");
            //Retrieve records from database and print them out
            String tableName = "Account" + this.getAccountNumber() + "Transaction";
            ResultSet results = stmt.executeQuery("SELECT * FROM " + tableName
                    + " ORDER BY transTime DESC LIMIT " + N);
            int iter = 0;
            while (results.next()) {
                res[iter++] = results.getString("transID") + " " + results.getString("transType") +
                        " " + results.getDouble("transAmount") + " " + results.getString("transTime");
                System.out.println(res[iter-1]);
            }
            results.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return res;
    }

}
