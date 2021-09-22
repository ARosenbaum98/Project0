package org.project.dataaccess;

import org.project.beans.Account;
import org.project.beans.Credit;
import org.project.beans.User;
import org.project.beans.activities.Activity;
import org.project.beans.activities.Deposit;
import org.project.beans.activities.Transfer;
import org.project.beans.activities.Withdrawal;

import java.text.DecimalFormat;
import java.util.List;

public abstract class AccessCredentialsObject {

    private static final DecimalFormat dollarFormat = new DecimalFormat("#.##");

    protected static AccessCredentialsObject singleton;
    protected static BankConnection db_connect;

    protected AccessCredentialsObject() {}

    public User getUser(){
        return null;
    }

    public static void connectToDatabase(BankConnection db) throws AccessError {
        if(db_connect==null){
            db_connect = db;
        }else{
            throw new AccessError("Database connection already established");
        }
    }

    public static void disconnectFromDatabase(){
        db_connect = null;
        singleton = null;
    }

    /**
     * @param user
     * @return returns list of account activity for a user
     * */
    protected List<Account> getUserAccounts(User user){

        List<Account> accounts = db_connect.getUserAccounts(user);
        return accounts;
    }

    /**
     * @param account Account
     * @return returns list of activities for an account
     * */
    protected List<Activity> getAccountActivity(Account account) {
        List<Activity> activities = db_connect.getAccountActivity(account);
        return activities;
    }

    /**
     * @param account Account
     * @return returns current balance of account
     */
    protected double getAccountBalance(Account account){
        List<Activity> activities = db_connect.getAccountActivity(account);
        double count = 0;
        for(Activity activity : activities){
            if(activity instanceof Deposit){
                count += activity.getAmount();
            }
            else if(activity instanceof Withdrawal){
                count -= activity.getAmount();
            }
            else if(activity instanceof Transfer){
                if(activity.getFromAccountId()==account.getAccountId()){
                    count -= activity.getAmount();
                }else{
                    count += activity.getAmount();
                }
            }
        }
        return Double.parseDouble(dollarFormat.format(count));
    }

    /**
     * @return returns a list of all users in the database
     */
    protected List<User> getAllUsers() {
        return db_connect.getAllUsers();
    }

    /**
     * @return returns a list of all accounts in the database
     */
    protected List<Account> getAllAccounts() {
        return db_connect.getAllAccounts();
    }


    /**
     * @return returns a list of all activities in the database
     */
    protected List<Activity> getAllActivities() {
        return db_connect.getAllActivities();
    }

    /**
     * @param username String - username to look up in system
     * @return Returns User object if they exist in the database. Returns null if they do not
     */
    protected User getUserByUsername(String username) {
        return db_connect.getUserByUsername(username);
    }

    /**
     * @param id int - user Id to look up in system
     * @return Returns User object if they exist in the database. Returns null if they do not
     */
    protected User getUserById(int id) {
        return db_connect.getUserById(id);
    }

    /**
     * @param accountNumber int - account number
     * @return returns Account object if it exists. Returns null if it does not
     */
    protected Account getByAccountNumber(String accountNumber) {
        return db_connect.getByAccountNumber(accountNumber);
    }
    /**
     * @param id int - account id
     * @return returns Account object if it exists. Returns null if it does not
     */
    protected Account getAccountById(int id) {
        return db_connect.getAccountById(id);
    }


    /**
     * @param id int - account id
     * @return returns Activity object if it exists. Returns null if it does not
     */
    protected Activity getActivityById(int id) {
        return db_connect.getActivityById(id);
    }

    /**
     * @param user User
     * @return returns true on successful insertion to the DB
     * */
    protected boolean insertNewUser(User user) {
        return db_connect.insertNewUser(user);
    }
    /**
     * @param account Account
     * @return returns true on successful insertion to the DB
     * */
    protected boolean insertNewAccount(Account account) {
        return db_connect.insertNewAccount(account);
    }

    /**
     * @param activity Activity
     * @return returns true on successful insertion to the DB
     * */
    protected boolean insertNewActivity(Activity activity) {
        return db_connect.insertNewActivity(activity);
    }


    /**
        @param  credit
     * @return returns true on successful insertion to the DB
     * */
    protected boolean insertNewCredit(Credit credit) {
        return db_connect.insertNewCredit(credit);
    }

    /**
     * @param id int - The database ID of the credit objecct
     * @return returns Credit object
     */
    protected Credit getCreditById(int id){
        return db_connect.getCreditById(id);
    }

    /**
     * @param user User object of user to look up
     * @return returns list of credit applications for a user
     * */
    protected List<Credit> getUserCredit(User user){
        return db_connect.getUserCredit(user);
    }

    /**
     * @param credit Credit - credit object to approve/deny
     * @param approve boolean - whether to approve the credit line
     */
    protected void approveCredit(Credit credit, boolean approve) {
        db_connect.approveCredit(credit, approve);
    }


    @Override
    public String toString() {
        return "AccessCredentialsObject";
    }


    public static class AccessError extends Throwable {
        public AccessError(String msg) {
            super(msg);
        }
    }

}
