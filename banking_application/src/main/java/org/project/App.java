package org.project;

import org.project.beans.Account;
import org.project.beans.User;
import org.project.dataaccess.AccessCredentialsObject;
import org.project.dataaccess.FileLogin;
import org.project.dataaccess.credentials.EmployeeAccessCredentials;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class App
{

    // Runtime Vars
    static Scanner in = new Scanner(System.in);
    static boolean exit = false;
    static boolean logging_out = false;
    static boolean go_back = false;
    static String userInput;

    // Connection vars
    static AccessCredentialsObject db_access;
    static User current_user;
    static String CONNECTION_TYPE = "FILE";

    // Dummy values
    static User myAdminUser = new User("3,ARose,password,Asher,Rosenbaum,1");


    public static void main( String[] args )
    {
        //testWithDummyUser(myAdminUser);
        //clientMenu();
        runApp();
    }

    public static void runApp(){
        while(!exit){

            login();
            if(exit) break;

            if(current_user.isEmployee()){
                employeeMenu();
            }

        }
        System.out.println();
        System.out.println("Goodbye!");
    }

    public static void login(){
        while(db_access==null && !exit) {
            System.out.println("Welcome User!");
            System.out.println("Please log in or type 'exit' to exit");
            System.out.println();

            System.out.println("Username: ");
            getUserInput();
            if(exit) break;
            String usernameAttempt = userInput;

            System.out.println("Password: ");
            getUserInput();
            if(exit) break;
            String passwordAttempt = userInput;

            establishConnection(usernameAttempt, passwordAttempt);

            if(db_access==null){
                System.out.println("Sorry, that information doesn't look right, please try again.");
                System.out.println();
            }
        }

    }

    public static void clientMenu(){

    }

    public static void employeeMenu(){
        while(!logging_out && !exit){
            System.out.println("Welcome, "+current_user.getFistName()+" "+current_user.getLastName());
            System.out.println("What would you like to do?");
            System.out.println();

            printEmployeeSelection();

        }
    }

    public static void printEmployeeSelection(){
        System.out.println(MenuOption.OPEN_ACCOUNT.menuCode+": create accounts for bank users");
        System.out.println(MenuOption.APPROVE_CREDIT.menuCode+": review credit requests");
        System.out.println();
        System.out.println("Type 'logout' to log out of your current session");

        MenuOption select = null ;
        while(select == null && !exit && !logging_out){

            setBackPoint();

            getUserInput();
            if (logging_out || exit) break;
            select = MenuOption.getEmployeActivityMenuOption(userInput);
        }

        if(select==MenuOption.OPEN_ACCOUNT){
            printCreateAccountMenu();
        }else if(select==MenuOption.APPROVE_CREDIT){
            printApproveCreditMenu();
        }
    }

    public static void printApproveCreditMenu(){
        System.out.println("TODO");
    }

    public static void printCreateAccountMenu(){
        System.out.println("Create Account");
        System.out.println("Type 'back' to go back, 'logout' to log out");
        System.out.println();
        System.out.println("Username of Account Holder: ");

        User userToMakeAccountFor = null;
        EmployeeAccessCredentials employee_access = (EmployeeAccessCredentials) db_access;

        while(userToMakeAccountFor==null && !exit && !logging_out && !go_back){
            getUserInput();
            setBackPoint();
            if(exit || logging_out || go_back) break;
            String username = userInput;

            userToMakeAccountFor = employee_access.getUserByUsername(username);
            if(userToMakeAccountFor==null) {
                System.out.println("That username does not exist");
                pauseForUser();
                break;
            }
            if(userToMakeAccountFor.isEmployee()){
                System.out.println("That account belongs to an employee. Please select a customer user account");
                pauseForUser();
                userToMakeAccountFor = null;
                break;
            }

            System.out.println("Username: "+userToMakeAccountFor.getUsername());
            System.out.println("First Name: "+userToMakeAccountFor.getFistName());
            System.out.println("Last Name: "+userToMakeAccountFor.getLastName());
            System.out.println();
            System.out.println("Does this look correct?");
            if(!getUserYesOrNo()) break;

            System.out.println("Please choose type of account to create: ");
            System.out.println(MenuOption.CREATE_CHECK_ACCOUNT.menuCode +": Create Checking Account");
            System.out.println(MenuOption.CREATE_SAVINGS_ACCOUNT.menuCode +": Create Savings Account");
            getUserInput();
            if(exit || logging_out || go_back) break;

            int accountId = -1;
            int userId = userToMakeAccountFor.getUserId();
            String accountNumber = Integer.toString(ThreadLocalRandom.current().nextInt(100000000, 999999999));
            String accountType = (userInput.equals(MenuOption.CREATE_CHECK_ACCOUNT.menuCode))? "checking":"savings";

            Account accountToInsert = new Account(accountId,userId,accountNumber,accountType);

            System.out.println();
            System.out.println("Account Details:");
            System.out.println("Account Holder Name: "+ userToMakeAccountFor.getFistName()+" "+userToMakeAccountFor.getLastName());
            System.out.println("Account Number: "+accountToInsert.getAccountNumber());
            System.out.println("Account Type: "+accountToInsert.getAccountDescriptor());
            System.out.println();
            System.out.println("Does this look correct to you?");

            getUserYesOrNo();
            if(exit || logging_out || go_back) break;

            if(userInput.equals("y")){
                ((EmployeeAccessCredentials) db_access).insertNewAccount(accountToInsert);

                System.out.println("Account Successfully Inserted!");
                pauseForUser();
            }

        }



    }


    private static void establishConnection(String user, String pass){
        switch(CONNECTION_TYPE){
            case("FILE"):
                db_access = FileLogin.login(user,pass);
                break;
            default:
                db_access = null;
        }
        if(db_access!=null){
            current_user = db_access.getUser();
        }
    }

    private static void setBackPoint(){
        go_back = false;
    }

    private static void testWithDummyUser(User dummyUser){
        establishConnection(dummyUser.getUsername(), dummyUser.getPassword());
    }

    private static void logout(){
        AccessCredentialsObject.disconnectFromDatabase();
        current_user = null;
    }

    private static void getUserInput(){
        userInput = in.nextLine().trim();
        if(userInput.equals("exit") ){
            exit = true;
        }
        if(userInput.equals("logout")){
            logout();
            logging_out = true;
        }
        if(userInput.equals("back")){
            go_back = true;
        }
    }

    private static void pauseForUser(){
        System.out.println();
        System.out.println("Press 'Enter' to continue...");
        in.nextLine();
    }

    private static boolean getUserYesOrNo(){
        userInput = "";
        while( !userInput.equals("y") && !userInput.equals("n")){
            System.out.print("(y/n): ");
            getUserInput();
        }
        return userInput.equals("y");
    }

    private enum MenuOption{
        // Employee Menu Options
        OPEN_ACCOUNT("1"), APPROVE_CREDIT("2"),

        // Create Account Options
        CREATE_CHECK_ACCOUNT("1"), CREATE_SAVINGS_ACCOUNT("2"),

        // Customer Account Options
        VIEW_ACCOUNTS("1"), DO_DEPOSIT("2"), DO_WITHDRAWAL("3"),
        DO_TRANSFER("4"), APPLY_FOR_CREDIT("5")
        ;

        public final String menuCode;

        MenuOption(String menuCode){
            this.menuCode = menuCode;
        }

        /*

    login to my account(s)
    view my balance
    deposit money
    withdraw money
    transfer money to other accounts
    view my transaction history
    apply for line(s) of credit
         */

        public static MenuOption getEmployeActivityMenuOption(String code){
            if(code.equals(OPEN_ACCOUNT.menuCode)){
                return OPEN_ACCOUNT;
            }else if(code.equals(APPROVE_CREDIT.menuCode)){
                return APPROVE_CREDIT;
            }else{
                return null;
            }
        }

        public static MenuOption getCreateAccountMenuOption(String code){
            if(code.equals(CREATE_CHECK_ACCOUNT.menuCode)){
                return CREATE_CHECK_ACCOUNT;
            }else if(code.equals(CREATE_SAVINGS_ACCOUNT.menuCode)){
                return CREATE_SAVINGS_ACCOUNT;
            }else{
                return null;
            }
        }

        public static MenuOption getCustomerActivityMenuOption(String code){
            if(code.equals(VIEW_ACCOUNTS.menuCode)){
                return VIEW_ACCOUNTS;
            }else if(code.equals(DO_DEPOSIT.menuCode)){
                return DO_DEPOSIT;
            }else if(code.equals(DO_WITHDRAWAL.menuCode)){
                return DO_WITHDRAWAL;
            }else if(code.equals(DO_TRANSFER.menuCode)){
                return DO_TRANSFER;
            }else if(code.equals(APPLY_FOR_CREDIT.menuCode)){
                return APPLY_FOR_CREDIT;
            }else{
                return null;
            }
        }
    }

}
