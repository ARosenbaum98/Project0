package org.project.dataaccess.credentials;

import org.project.beans.Account;
import org.project.beans.Credit;
import org.project.beans.User;
import org.project.beans.activities.Activity;
import org.project.dataaccess.AccessCredentialsObject;

import java.util.List;

public class CustomerAccessCredentials extends AccessCredentialsObject {

    private User userLoggedIn;

    private CustomerAccessCredentials(User user){
        super();
        userLoggedIn=user;
    }

    public User getUser(){
        return userLoggedIn;
    }

    public static AccessCredentialsObject newInstance(User user) throws AccessError {
        if(singleton==null){
            singleton = new CustomerAccessCredentials(user);

            return AccessCredentialsObject.singleton;
        }else{
            throw new AccessError("Credential Object Already Exists");
        }
    }

    public List<Account> getAccounts(){
        return super.getUserAccounts(userLoggedIn);
    }

    @Override
    public Account getByAccountNumber(String number){
        Account account = super.getByAccountNumber(number);
        if(account!= null && account.getUserId() == userLoggedIn.getUserId()){
            return account;
        }
        return null;
    }

    @Override
    public List<Activity> getAccountActivity(Account account) {
        if(account.getUserId()==userLoggedIn.getUserId()){
            return super.getAccountActivity(account);
        }
        return null;
    }

    @Override
    public double getAccountBalance(Account account){
        List<Account> userLoggedInAccounts = getAccounts();
        if(userLoggedInAccounts.contains(account)){
            return super.getAccountBalance(account);
        }
        return 0;
    }

    @Override
    public boolean insertNewActivity(Activity activity) {
        Account fromAccount = super.getAccountById(activity.getFromAccountId());
        Account toAccount = super.getAccountById(activity.getToAccountId());

        boolean validAccount = false;
        if(fromAccount!=null) validAccount = fromAccount.getUserId()==userLoggedIn.getUserId();
        if(toAccount!=null && !validAccount) validAccount = toAccount.getUserId()==userLoggedIn.getUserId();

        if(validAccount){
            return super.insertNewActivity(activity);
        }

        return false;
    }

    @Override
    public boolean insertNewCredit(Credit credit){
        if(credit.getUserId()==userLoggedIn.getUserId()){
            return super.insertNewCredit(credit);
        }
        return false;
    }

    public List<Credit> getUserCredit(){
        return super.getUserCredit(userLoggedIn);
    }



    @Override
    public String toString() {
        return "CustomerAccessCredentials{" +
                "userLoggedIn=" + userLoggedIn +
                '}';
    }


}
