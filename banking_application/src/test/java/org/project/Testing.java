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


public class Testing {

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


    @Test
    public void testLogInEmployee() throws ClassNotFoundException {
        AccessCredentialsObject.disconnectFromDatabase();

        access = SQLLogin.login("ARose","password");

        Assert.assertTrue(access instanceof EmployeeAccessCredentials);

    }

    @Test
    public void testLogInCustomer() throws ClassNotFoundException {
        AccessCredentialsObject.disconnectFromDatabase();

        access = SQLLogin.login("JJameson22","password2");

        Assert.assertTrue(access instanceof CustomerAccessCredentials);
    }

    @Test
    public void testLogInFailure(){
        AccessCredentialsObject.disconnectFromDatabase();

        access = SQLLogin.login("notauser","");

        Assert.assertTrue(access == null);
    }

    @Test
    public void testGetAllUsers(){
        List<User> allUsers = fullAccess.getAllUsers();

        Assert.assertNotNull(allUsers);
    }

    @Test
    public void testGetAllAccounts(){
        List<Account> allAccounts = fullAccess.getAllAccounts();

        Assert.assertNotNull(allAccounts);
    }

    @Test
    public void testGetAllActvities(){
        List<Activity> allActivities= fullAccess.getAllActivities();

        Assert.assertNotNull(allActivities);
    }

    @Test
    public void testInsertUser(){

        User user = new User(-1,"FakeUser","password123","Fakey", "User", false);
        fullAccess.insertNewUser(user);

        Assert.assertNotNull(fullAccess.getUserByUsername("JJoijsojs"));
    }

    @Test
    public void testInsertAccount(){

        User user = fullAccess.getUserByUsername("AMBobby");
        Account account = new Account(-1,user.getUserId(),"43534233","Checking");

        fullAccess.insertNewAccount(account);

        Assert.assertNotNull(fullAccess.getUserAccounts(user));

    }

    @Test
    public void testInsertActivity(){

        User user = fullAccess.getUserByUsername("AMBobby");
        Account account = fullAccess.getUserAccounts(user).get(0);

        User user2 = fullAccess.getUserByUsername("JJameson22");
        Account account2 = fullAccess.getUserAccounts(user2).get(0);

        Activity deposit = new Deposit(-1, account.getAccountId(), 10);
        Activity withdrawal = new Withdrawal(-1,account.getAccountId(),5);
        Activity transfer = new Transfer(-1, account.getAccountId(),account2.getAccountId(),5);

        Activity deposit2 = new Deposit(-1, account2.getAccountId(),30);
        Activity withdrawal2 = new Withdrawal(-1, account2.getAccountId(), 25);
        Activity transfer2 = new Transfer(-1, account2.getAccountId(), account.getAccountId(),5);

        fullAccess.insertNewActivity(deposit);
        fullAccess.insertNewActivity(withdrawal);
        fullAccess.insertNewActivity(transfer);


        fullAccess.insertNewActivity(deposit2);
        fullAccess.insertNewActivity(withdrawal2);
        fullAccess.insertNewActivity(transfer2);
    }


    @Test
    public void testInsertCredit(){

        User user = new User("2,JJameson22,password2,false,Jemall,Jameson");
        Credit credit = new Credit(-1, user.getUserId(), 400, 0.025,false,true);

        fullAccess.insertNewCredit(credit);

        List<Credit> creditAccounts = fullAccess.getUserCredit(user);

        Assert.assertNotNull(creditAccounts);

    }


    @Test
    public void testUserCreation(){

        int id = 20;
        String username = "twoThreeFour";
        String password = "123abc";
        String fname = "Faris";
        String lname = "Victoria";
        boolean employee = false;

        User user = new User(id, username, password, fname, lname, employee);

        Assert.assertEquals(id, user.getUserId());
        Assert.assertEquals(username, user.getUsername());
        Assert.assertEquals(password, user.getPassword());
        Assert.assertEquals(fname, user.getFistName());
        Assert.assertEquals(lname, user.getLastName());
        Assert.assertEquals(employee, user.isEmployee());

        user = new User("20,helloworld,123,fname,lname,false");
        Assert.assertNotNull(user.toString());
    }

    @Test
    public void testAccountBean(){
        int id = 40;
        int userId = 10;
        String accountNumber = "30242343";
        String accountType = "Savings";

        Account account = new Account(id, userId, accountNumber, accountType);

        Assert.assertEquals(id, account.getAccountId());
        Assert.assertEquals(userId, account.getUserId());
        Assert.assertEquals(accountNumber, account.getAccountNumber());
        Assert.assertEquals(accountType, account.getAccountDescriptor());

        account = new Account("20,12,23423243,Checking");

        Assert.assertNotNull(account.toString());
    }

    @Test
    public void testCreditBean(){
        int id = 20;
        int userId = 40;
        double loanAmount = 394.20;
        double interestRate = .23;
        boolean pendingApproval = false;
        boolean approved = true;

        Credit credit = new Credit(id, userId, loanAmount, interestRate, pendingApproval, approved);
    }




}
