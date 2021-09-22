package org.project;

import org.junit.*;
import org.project.beans.Account;
import org.project.beans.User;

public class BeanTesting {

    @Test
    public void testUserCreation(){

        int id = 20;
        String username = "hell0world";
        String password = "123abc";
        String fname = "fname";
        String lname = "lname";
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
}
