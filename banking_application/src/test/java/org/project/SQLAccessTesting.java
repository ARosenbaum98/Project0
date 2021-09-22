package org.project;

import org.junit.*;
import org.project.beans.Account;
import org.project.beans.Credit;
import org.project.beans.User;
import org.project.beans.activities.Activity;
import org.project.beans.activities.Deposit;
import org.project.beans.activities.Transfer;
import org.project.beans.activities.Withdrawal;
import org.project.dataaccess.AccessCredentialsObject;
import org.project.dataaccess.SQLLogin;
import org.project.dataaccess.credentials.CustomerAccessCredentials;
import org.project.dataaccess.credentials.EmployeeAccessCredentials;
import org.project.dataaccess.credentials.FullAccessCredentials;

import java.util.List;


public class SQLAccessTesting {

    AccessCredentialsObject access;

    FullAccessCredentials fullAccess;

    @Before
    public void init(){
        fullAccess = (FullAccessCredentials) SQLLogin.login_test();
    }


    @After
    public void reset(){
        access = null;
        AccessCredentialsObject.disconnectFromDatabase();
    }


    @Ignore
    public void testLogInEmployee() throws ClassNotFoundException {
        AccessCredentialsObject.disconnectFromDatabase();

        access = SQLLogin.login("ARose","password");

        Assert.assertTrue(access instanceof EmployeeAccessCredentials);

    }

    @Ignore
    public void testLogInCustomer() throws ClassNotFoundException {
        AccessCredentialsObject.disconnectFromDatabase();

        access = SQLLogin.login("JJameson22","password2");

        Assert.assertTrue(access instanceof CustomerAccessCredentials);
    }

    @Ignore
    public void testLogInFailure(){
        AccessCredentialsObject.disconnectFromDatabase();

        access = SQLLogin.login("notauser","");

        Assert.assertTrue(access == null);
    }

    @Ignore
    public void testGetAllUsers(){
        List<User> allUsers = fullAccess.getAllUsers();

        Assert.assertNotNull(allUsers);
    }

    @Ignore
    public void testGetAllAccounts(){
        List<Account> allAccounts = fullAccess.getAllAccounts();

        Assert.assertNotNull(allAccounts);
    }

    @Ignore
    public void testGetAllActvities(){
        List<Activity> allActivities= fullAccess.getAllActivities();

        Assert.assertNotNull(allActivities);
    }

    @Ignore
    public void testInsertUser(){

        User user = new User(-1,"BjSimmons","bjcconnect12","BJ", "Simmons", false);
        fullAccess.insertNewUser(user);

        Assert.assertNotNull(fullAccess.getUserByUsername("BjSimmons"));
    }

    @Ignore
    public void testInsertAccount(){

        User user = fullAccess.getUserByUsername("AMBobby");
        Account account = new Account(-1,user.getUserId(),"88229012","Savings");

        fullAccess.insertNewAccount(account);

        Assert.assertNotNull(fullAccess.getUserAccounts(user));

    }

    @Ignore
    public void testInsertActivity(){

        User user = fullAccess.getUserByUsername("AMBobby");
        Account account = fullAccess.getUserAccounts(user).get(0);

        User user2 = fullAccess.getUserByUsername("JJameson22");
        Account account2 = fullAccess.getUserAccounts(user2).get(0);

        Activity deposit = new Deposit(-1, account.getAccountId(), 2000);
        Activity withdrawal = new Withdrawal(-1,account.getAccountId(),250);
        Activity transfer = new Transfer(-1, account.getAccountId(),account2.getAccountId(),20);

        Activity deposit2 = new Deposit(-1, account2.getAccountId(),40);
        Activity withdrawal2 = new Withdrawal(-1, account2.getAccountId(), 30);
        Activity transfer2 = new Transfer(-1, account2.getAccountId(), account.getAccountId(),5);

        fullAccess.insertNewActivity(deposit);
        fullAccess.insertNewActivity(withdrawal);
        fullAccess.insertNewActivity(transfer);


        fullAccess.insertNewActivity(deposit2);
        fullAccess.insertNewActivity(withdrawal2);
        fullAccess.insertNewActivity(transfer2);
    }


    @Ignore
    public void testInsertCredit(){

        User user = new User("2,JJameson22,password2,false,Jemall,Jameson");
        Credit credit = new Credit(-1, user.getUserId(), 400, 0.025,false,true);

        fullAccess.insertNewCredit(credit);

        List<Credit> creditAccounts = fullAccess.getUserCredit(user);

        Assert.assertNotNull(creditAccounts);

    }




}
