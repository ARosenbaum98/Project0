package org.project;

import org.junit.*;

import org.project.beans.activities.*;
import org.project.dataaccess.AccessCredentialsObject;
import org.project.dataaccess.credentials.EmployeeAccessCredentials;
import org.project.dataaccess.credentials.FullAccessCredentials;
import org.project.dataaccess.FileLogin;
import org.project.beans.Account;
import org.project.beans.User;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FileAccessTesting {

     //BankFileAccess fileObj;
     EmployeeAccessCredentials employeeFileAccessSession;


     //Test data from the CSVs
    /*
    users.csv
        1,dedwards,Valley$Shore24,Debrah,Edwards,1
        2,martian.cully,Apples4Georgia!,Martian,Cully,0
        3,ARose,password,Asher,Rosenbaum,1
        4,HMWellington,helloworld!2,Harrison,Wellington,0
    accounts.csv
        1,2,88861234,checking
        2,2,84883939,savings
        3,3,44833234,savings
    activity.csv
        1,,2,5000,1
        2,,3,1000,1
        3,,2,500,1
        4,2,,250,2
        5,3,,200,2
        6,,3,300,1
     */

    final static String USERFILE = "src/test/files/users.csv";
    final static String ACCOUNTFILE = "src/test/files/accounts.csv";
    final static String ACIVITIYFILE = "src/test/files/activity.csv";

     final static List<User> testUserFile = Arrays.asList(
             new User("1,dedwards,Valley$Shore24,Debrah,Edwards,1"),
             new User("2,martian.cully,Apples4Georgia!,Martian,Cully,0"),
             new User("3,ARose,password,Asher,Rosenbaum,1"),
             new User("4,HMWellington,helloworld!2,Harrison,Wellington,0")
     );
    final static List<Account> testAccountFile = Arrays.asList(
            new Account(1,2,"88861234","checking"),
            new Account(2,2,"84883939","savings"),
            new Account(3,3,"44833234","savings")

    );


    static Activity a1 = new Deposit(1,2,5000.0);
    static Activity a2 = new Deposit(2,3,1000);
    static Activity a3 = new Deposit(3,2,500);
    static Activity a4 = new Withdrawal(4,2,250);
    static Activity a5 = new Withdrawal(5,3,200);
    static Activity a6 = new Deposit(6,3,300);
    static Transfer a7 = new Transfer(7,3,2,300);
    final static List<Activity> testActivityFile =  Arrays.asList(a7,a1,a2,a3,a4,a5,a6);

    List<User> UserArrayList;
    List<Account> AccountArrayList;
    List<Activity> ActivityArrayList;

    @Before
    public  void init(){
        //fileObj = new BankFileAccess();
        employeeFileAccessSession = (EmployeeAccessCredentials) FileLogin.login("ARose","password");
        UserArrayList = employeeFileAccessSession.getAllUsers();
        AccountArrayList = employeeFileAccessSession.getAllAccounts();
        ActivityArrayList = employeeFileAccessSession.getAllActivities();

    }

    @After
    public void disconnectAccessManager(){
        AccessCredentialsObject.disconnectFromDatabase();
    }

    @After
    public void roleBackFileChanges(){
        clearFile(USERFILE);
        clearFile(ACCOUNTFILE);
        clearFile(ACIVITIYFILE);

        int i = 0;
        for(User user : testUserFile){
            appendToFile(USERFILE,((i++==0)?"":"\n")+ user.stringify());
        }

        i = 0;
        for(Account account : testAccountFile){
            appendToFile(ACCOUNTFILE, ((i++==0)?"":"\n")+ account.stringify());
        }

        i = 0;
        for(Activity activity : testActivityFile){
            appendToFile(ACIVITIYFILE, ((i++==0)?"":"\n")+ activity.stringify());
        }
    }

    @Test
    public void AssertTrue(){
        Assert.assertTrue(true);
    }


    @Test
    public void TestDummyArrays(){
        //Check that the dummy arrays match the csv files

        Assert.assertEquals(employeeFileAccessSession.getAllUsers(),UserArrayList);
        Assert.assertEquals(employeeFileAccessSession.getAllAccounts(),AccountArrayList);
        Assert.assertEquals(employeeFileAccessSession.getAllActivities(),ActivityArrayList);
    }

    @Test
    public void TestFullAccess_GetUserAccount(){

        String userString = "2,martian.cully,Apples4Georgia!,Martian,Cully,0";

        User user = new User(userString);

        List<Account> actual = employeeFileAccessSession.getUserAccounts(user);
        List<Account> expected = new ArrayList<>();

        for(Account account : AccountArrayList){
            if(account.getUserId() == user.getUserId()){
                expected.add(account);
            }
        }

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void TestFullAccess_GetAccountActivity(){


        Account actualAccount = employeeFileAccessSession.getAccountById(2);
        Account expectedAccount = null;

        for(Account account : AccountArrayList){
            if(account.getAccountId() == 2){
                expectedAccount = account;
            }
        }

        // Test that the correct account was taken
        Assert.assertEquals(expectedAccount, actualAccount);



        List<Activity> expectedActivities = new ArrayList<Activity>();
        List<Activity> actualActivities = employeeFileAccessSession.getAccountActivity(actualAccount);

        for(Activity activity : ActivityArrayList){
            if(activity.isAssociatedAccountId(expectedAccount.getAccountId())){
                expectedActivities.add(activity);
            }
        }

        // Test that the activity list is returned correctly
        Assert.assertEquals(expectedActivities,actualActivities);


    }

    @Test
    public void TestFullAccess_InsertIntoDB(){

        User newUser = new User("5,Jewel42,yellowcat3,Mary,Jewel,0");
        employeeFileAccessSession.insertNewUser(newUser);

        Assert.assertNotEquals(UserArrayList, employeeFileAccessSession.getAllUsers());

        Account newAccount = new Account("5,3,44773234,savings");
        employeeFileAccessSession.insertNewAccount(newAccount);

        Assert.assertNotEquals(AccountArrayList, employeeFileAccessSession.getAllAccounts());

        Activity newActivity = ActivityFactory.getActivity("7,,3,300,1");
        employeeFileAccessSession.insertNewActivity(newActivity);

        Assert.assertNotEquals(ActivityArrayList, employeeFileAccessSession.getAllActivities());

    }

    private boolean clearFile(String path){
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(path);
            bw = new BufferedWriter(fw);
            bw.write("");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private boolean appendToFile(String path, String data){
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(path, true);
            bw = new BufferedWriter(fw);
            bw.write(data);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
