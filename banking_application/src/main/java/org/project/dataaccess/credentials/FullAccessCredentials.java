package org.project.dataaccess.credentials;

import org.project.beans.Account;
import org.project.beans.Credit;
import org.project.beans.User;
import org.project.beans.activities.Activity;
import org.project.dataaccess.AccessCredentialsObject;


import java.util.List;

public class FullAccessCredentials extends AccessCredentialsObject {

    private User userLoggedIn;

    public FullAccessCredentials(){
        super();
    }


    public static AccessCredentialsObject newInstance(User user) throws AccessError {
        if(singleton==null){
            singleton = new FullAccessCredentials();
            return singleton;
        }else{
            throw new AccessError("Credential object already exists");
        }
    }

    public static AccessCredentialsObject newInstance() throws AccessError {
        return FullAccessCredentials.newInstance(null);
    }


    @Override
    public double getAccountBalance(Account account) {
        return super.getAccountBalance(account);
    }

    @Override
    public Account getByAccountNumber(String accountNumber) {
        return super.getByAccountNumber(accountNumber);
    }

    @Override
    public boolean insertNewCredit(Credit credit) {
        return super.insertNewCredit(credit);
    }

    @Override
    public Credit getCreditById(int id) {
        return super.getCreditById(id);
    }

    @Override
    public List<Account> getUserAccounts(User user){
        return super.getUserAccounts(user);
    }

    @Override
    public List<Activity> getAccountActivity(Account account) {
        return super.getAccountActivity(account);
    }

    @Override
    public List<User> getAllUsers() {
        return super.getAllUsers();
    }

    @Override
    public List<Account> getAllAccounts() {
        return super.getAllAccounts();
    }

    @Override
    public List<Activity> getAllActivities() {
        return super.getAllActivities();
    }

    @Override
    public List<Credit> getUserCredit(User user) {
        return super.getUserCredit(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return super.getUserByUsername(username);
    }

    @Override
    public User getUserById(int id) {
        return super.getUserById(id);
    }

    @Override
    public Account getAccountById(int id) {
        return super.getAccountById(id);
    }

    @Override
    public Activity getActivityById(int id) {
        return super.getActivityById(id);
    }

    @Override
    public boolean insertNewUser(User user) {
        return super.insertNewUser(user);
    }

    @Override
    public boolean insertNewAccount(Account account) {
        return super.insertNewAccount(account);
    }

    @Override
    public boolean insertNewActivity(Activity activity) {
        return super.insertNewActivity(activity);
    }

    @Override
    public String toString() {
        return "FullAccessCredentials";
    }
}
