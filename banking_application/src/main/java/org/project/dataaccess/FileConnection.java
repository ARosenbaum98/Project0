package org.project.dataaccess;

import org.apache.log4j.Logger;
import org.project.beans.Account;
import org.project.beans.User;
import org.project.beans.activities.*;
import org.project.dataaccess.credentials.EmployeeAccessCredentials;
import org.project.dataaccess.credentials.UserAccessCredentials;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class FileConnection implements Connection{

    private final Logger errorLog = Logger.getLogger(FileConnection.class);

    final String DIR = "files/";
    final String USERFILE = DIR+"users.csv";
    final String ACCOUNTFILE = DIR+"accounts.csv";
    final String ACTIVITYFILE = DIR+"activity.csv";

    private  List<User> userRecord;
    private  List<Account> accountRecord;
    private  List<Activity> activityRecord;

    private static FileConnection singleton = new FileConnection();

    private FileConnection(){}

    static FileConnection getInstance() {
        return FileConnection.singleton;
    }


    public AccessCredentialsObject login(String username, String password) {
        populateUserFile();
        for(User user : userRecord){
            if(user.getUsername().equals(username)){
                if(user.getPassword().equals(password)){

                    if(user.isEmployee()){
                        try {
                            return EmployeeAccessCredentials.newInstance(user);
                        }catch(AccessCredentialsObject.AccessError e){
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            return UserAccessCredentials.newInstance(user);
                        }catch(AccessCredentialsObject.AccessError e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        depopulateUserFile();
        return null;
    }

    public List<User> getAllUsers() {
        try {
            populateUserFile();
            return userRecord;
        }finally{
            depopulateUserFile();
        }
    }

    public List<Account> getAllAccounts() {
        try {
            populateAccountFile();
            return accountRecord;
        }finally{
            depopulateAccountFile();
        }
    }

    public List<Activity> getAllActivities() {
        try {
            populateActivityFile();
            return activityRecord;
        }
        finally{
            depopulateActivityFile();
        }
    }

    public User getUserById(int id) {
        populateUserFile();
        User searchUser = null;

        for(User user : userRecord){
            if(user.getUserId()==id){
                searchUser = user;
                break;
            }
        }

        depopulateUserFile();
        return searchUser;
    }

    public Account getAccountById(int id) {
        populateAccountFile();
        Account searchAccount = null;

        for(Account account : accountRecord){
            if(account.getAccountId()==id){
                searchAccount = account;
                break;
            }
        }

        depopulateAccountFile();
        return searchAccount;
    }

    public Activity getActivityById(int id) {
        populateActivityFile();
        Activity searchActivity = null;

        for(Activity activity : activityRecord){
            if(activity.getActivityId()==id){
                searchActivity = activity;
                break;
            }
        }

        depopulateActivityFile();
        return searchActivity;
    }

    public List<Account> getUserAccounts(User user){
        populateAccountFile();
        List<Account> accounts = new ArrayList<Account>();
        for(Account account : accountRecord){
            if(account.getUserId()==user.getUserId()){
                accounts.add(account);
            }
        }
        depopulateAccountFile();
        return accounts;
    }

    public List<Activity> getAccountActivity(Account account) {
        populateActivityFile();
        List<Activity> activities = new ArrayList<Activity>();
        for(Activity activity : activityRecord){
            if(activity.isAssociatedAccountId(account.getAccountId())){
                activities.add(activity);
            }
        }
        depopulateActivityFile();
        return activities;
    }

    public boolean insertNewUser(User user) {
        return appendToFile(USERFILE, user.stringify());
    }

    public boolean insertNewAccount(Account account) {
        return appendToFile(ACCOUNTFILE, account.stringify());
    }

    public boolean insertNewActivity(Activity activity) {
        return appendToFile(ACTIVITYFILE, activity.stringify());
    }

    @Override
    public User getUserByUsername(String username) {
        populateUserFile();
        User searchUser = null;

        for(User user : userRecord){
            if(user.getUsername().equals(username)){
                searchUser = user;
                break;
            }
        }

        depopulateUserFile();
        return searchUser;
    }

    /**
     * Populate the userRecord from the users file
     */
    private void populateUserFile(){
        depopulateUserFile();

        List<List<String>> rawList = getCSVAsStringList(USERFILE);

        for(List<String> userStringList : rawList){
            if(userStringList.isEmpty()){
                errorLog.error("A row in file "+USERFILE+" is empty");
                throw new NullPointerException("A row in file "+USERFILE+" is empty");
            }
            User user = new User(userStringList);
            userRecord.add(user);
        }
    }

    /**
     * Clears the userRecord
     */
    private void depopulateUserFile(){
        userRecord = new ArrayList<User>();
    }

    /**
     * Populate the accountRecord from the account files
     */
    private void populateAccountFile(){
        depopulateAccountFile();

        List<List<String>> rawList = getCSVAsStringList(ACCOUNTFILE);

        for(List<String> accountStringList : rawList){
            if(accountStringList.isEmpty()){
                errorLog.error("A row in file "+ACCOUNTFILE+" is empty");
                throw new NullPointerException("A row in file "+ACCOUNTFILE+" is empty");
            }
            Account account = new Account(accountStringList);
            accountRecord.add(account);
        }

    }

    /**
     * Clears the accountRecord
     */
    private void depopulateAccountFile(){
        accountRecord = new ArrayList<Account>();
    }

    /**
     * Populate the activityRecord from the account files
     */
    private void populateActivityFile(){
        depopulateActivityFile();

        List<List<String>> rawList = getCSVAsStringList(ACTIVITYFILE);

        for(List<String> activityStringList : rawList){
            if(activityStringList.isEmpty()){
                errorLog.error("A row in file "+ACTIVITYFILE+" is empty");
                throw new NullPointerException("A row in file "+ACTIVITYFILE+" is empty");
            }

            Activity activity = ActivityFactory.getActivity(activityStringList);
            activityRecord.add(activity);
        }

    }

    /**
     * Clears the activityRecord
     */
    private void depopulateActivityFile(){
        activityRecord = new ArrayList<Activity>();
    }

    private boolean appendToFile(String path, String data){
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(path, true);
            bw = new BufferedWriter(fw);
            bw.write("\n"+data);
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


    /**
     *
     * @param path String - path to the file to be read
     * @return returns a list of string lists that corresponds to data entries
     */
    private List<List<String>> getCSVAsStringList(String path){
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
            return records;
        } catch (FileNotFoundException e) {
            errorLog.error("File Not Found For ../"+path);
            e.printStackTrace();
        } catch (IOException e) {
            errorLog.error("IO Exception while opening file at ../"+path);
            e.printStackTrace();
        }
        return null;
    }

}
