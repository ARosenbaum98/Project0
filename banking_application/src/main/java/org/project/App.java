package org.project;

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


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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

    // BankConnection vars
    static AccessCredentialsObject db_access;
    static User current_user;
    static String CONNECTION_TYPE = "SQL";

    // Dummy values
    static User myAdminUser = new User("3,ARose,password,Asher,Rosenbaum,1");
    static User myClientUser = new User("2,JJameson22,password2,Jemall,Jameson,false");


    public static void main( String[] args )
    {
        runApp();
    }

    public static void runApp(){
        while(!exit){

            login();
            if(exit) break;

            if(current_user.isEmployee()){
                employeeMenu();
            }else{
                customerMenu();
            }

        }
        System.out.println();
        System.out.println("Goodbye!");
    }

    public static void login(){
        while(db_access==null && !exit) {
            logging_out = false;
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

    public static void customerMenu(){
        while(!logging_out && !exit){
            printWelcomeMessage();

            printCustomerSelection();
        }
    }

    public static void employeeMenu(){
        while(!logging_out && !exit){
            printWelcomeMessage();

            printEmployeeSelection();

        }
    }

    public static void printCustomerSelection() {
        MenuOption select = null;
        while (select == null && !exit && !logging_out) {

            System.out.println(MenuOption.VIEW_ACCOUNTS.menuCode + ": View my accounts");
            System.out.println(MenuOption.DO_DEPOSIT.menuCode + ": Make a deposit");
            System.out.println(MenuOption.DO_WITHDRAWAL.menuCode + ": Make a withdrawal");
            System.out.println(MenuOption.DO_TRANSFER.menuCode + ": Make a transfer");
            System.out.println(MenuOption.APPLY_FOR_CREDIT.menuCode + ": Apply for a line of credit");
            System.out.println();
            System.out.println("Type 'logout' to log out of your current session");


            setBackPoint();

            getUserInput();
            if (logging_out || exit) break;
            select = MenuOption.getCustomerActivityMenuOption(userInput);


            if (select == MenuOption.VIEW_ACCOUNTS) {
                printViewAccountMenu();
            } else if (select == MenuOption.DO_DEPOSIT) {
                doDepositMenu();
            } else if (select == MenuOption.DO_WITHDRAWAL) {
                doWithdrawalMenu();
            } else if (select == MenuOption.DO_TRANSFER) {
                doTransferMenu();
            } else if (select == MenuOption.APPLY_FOR_CREDIT) {
                applyForCredit();
            }
            select = null;
        }

    }

    private static void applyForCredit() {
        printEmptyLine();

        String rules ="Approval Process for Credit"+
                "\n"+
                "     For Amounts less than 500\n" +
                "               Denied: User has less than 100 in their accounts\n" +
                "               Approved: User has over $100 and more than 5 transactions\n" +
                "               Pending: All others\n"+
                "\n" +
                "               Interest Rate will be 13%"+
                "     \n" +
                "            For Amounts between 500 and 5000\n" +
                "               Denied: User has less than 5 transactions or have less than 500 dollars in assests.\n" +
                "               Approved: User has more than 10 transactions and more than 1000 dollars\n" +
                "               Pending: All others\n" +
                "\n" +
                "               Interest Rate will 9%"+
                "     \n" +
                "            For Amounts between 5000 and 10,000\n" +
                "               Denied: if user has less than 10 transactions and less than $2500\n" +
                "               Pending: all others"+
                "\n" +
                "               Interest Rate will be 5%";
        l1: while(true){
            double amount = 0;
            double interest = 0;
            System.out.println(rules);
            while(true){
                System.out.println("Input how much you'd like to borrow or type 'back' to return to the previous menu");
                getUserInput();
                if(exit||logging_out||go_back) break l1;


                try{
                    amount = Double.parseDouble(userInput);
                    break;
                }catch(NumberFormatException e){
                    System.out.println("Unable to read amount");
                }
            }
            if(amount<500){
                interest = 0.15;
            }else if(amount<5000){
                interest = 0.09;
            }else if(amount<10000){
                interest = 0.05;
            }

            ApprovalLevel approvalLevel = getCreditApproval(amount);
            if(approvalLevel == ApprovalLevel.APPROVED){

                Credit credit = new Credit(-1, db_access.getUser().getUserId(), amount, interest,false, true);
                ((CustomerAccessCredentials)db_access).insertNewCredit(credit);

                System.out.println("You've been automatically approved.");
                System.out.println("Your credit line has been added to your account.");
                pauseForUser();

                break;
            }else if(approvalLevel == ApprovalLevel.PENDING){
                System.out.println("Your credit line has been added to your account and is now pending employee approval.");
                Credit credit = new Credit(-1, db_access.getUser().getUserId(), amount, interest,true, false);
                ((CustomerAccessCredentials)db_access).insertNewCredit(credit);
                pauseForUser();
                break;
            }else {
                System.out.println("Your request has been automatically denied. You did not meet the minimum account criteria");
                pauseForUser();
                break;
            }

        }
    }


    /**
     * @param amount Double - Amount requested
     * @return Returns an approval level:
     *
     *       For Amounts less than 500
     *          Denied: User has less than 100 in their accounts
     *          Approved: User has over $100 and more than 5 transactions
     *          Pending: All others
     *
     *       For Amounts between 500 and 5000
     *          Denied: User has less than 5 transactions or have less than 500 dollars in assests.
     *          Approved: User has more than 10 transactions and more than 1000 dollars
     *          Pending: All others
     *
     *       For Amounts between 5000 and 10,000
     *          Denied: if user has less than 10 transactions and less than $2500
     *          Pending: all others
     *
     *       For amounts over 10,000
     *          Denied: all
     *
     *
     */
    private static ApprovalLevel getCreditApproval(double amount){
        List<Account> accounts = ((CustomerAccessCredentials)db_access).getAccounts();
        double assets = 0;
        int history = 0;
        for(Account account : accounts){
            List<Activity> activities = ((CustomerAccessCredentials)db_access).getAccountActivity(account);
            assets+=((CustomerAccessCredentials)db_access).getAccountBalance(account);
            history+=activities.size();
        }

        if (amount < 500) {
            if(assets<100){
                return ApprovalLevel.DENIED;
            }else if(history > 5){
                return ApprovalLevel.APPROVED;
            } else{
                return ApprovalLevel.PENDING;
            }
        }else if(amount>=500 && amount<=5000){
            if(history < 5 || assets < 500){
                return ApprovalLevel.DENIED;
            }else if(history > 10 && assets > 1000){
                return ApprovalLevel.APPROVED;
            }else{
                return ApprovalLevel.PENDING;
            }
        }else if(amount>5000 && amount<10000){
            if(history < 10 && assets > 2500){
                return ApprovalLevel.PENDING;
            }
            return ApprovalLevel.DENIED;
        }else{
            return ApprovalLevel.DENIED;
        }
    }

    public enum ApprovalLevel{
        APPROVED, DENIED, PENDING;
    }

    public static void doDepositMenu(){

        Deposit deposit = null;

        while(deposit == null){
            printEmptyLine();

            Account account = null;
            while(account==null) {
                printViewAccounts();
                System.out.println("Type the account number you would like to deposit to: ");
                getUserInput();
                if (logging_out || exit || go_back) break;

                account = ((CustomerAccessCredentials) db_access).getByAccountNumber(userInput);
                if(account!=null) break;

                System.out.println("That account is not associated with this user. Please try again.");
                pauseForUser();
            }

            if (logging_out || exit || go_back) break;
            while(true){
                System.out.println("Account Balance: "+((CustomerAccessCredentials) db_access).getAccountBalance(account));
                System.out.println("How much would you like to deposit?");
                getUserInput();
                if (logging_out || exit || go_back) break;
                try{
                    double amount = Double.parseDouble(userInput);
                    deposit = new Deposit(-1, account.getAccountId(),amount);
                    if(((CustomerAccessCredentials) db_access).insertNewActivity(deposit)){
                        System.out.println("Deposit Successful!");
                        pauseForUser();
                    }else{
                        System.out.println("An error has occurred. Please try again");
                        pauseForUser();
                    }
                    break;
                }catch(NumberFormatException e){
                    System.out.println("Input not recognized, try again.");
                }

            }

        }
    }

    public static void doWithdrawalMenu(){

        Withdrawal withdrawal = null;

        while(withdrawal == null){
            printEmptyLine();

            Account account = null;
            while(account==null) {
                printViewAccounts();
                System.out.println("Type the account number you would like to withdraw from: ");
                getUserInput();
                if (logging_out || exit || go_back) break;

                account = ((CustomerAccessCredentials) db_access).getByAccountNumber(userInput);
                if(account!=null) break;

                System.out.println("That account is not associated with this user. Please try again.");
                pauseForUser();
            }

            if (logging_out || exit || go_back) break;
            while(true){
                System.out.println("Account Balance: "+((CustomerAccessCredentials) db_access).getAccountBalance(account));
                System.out.println("How much would you like to withdraw?");
                getUserInput();
                if (logging_out || exit || go_back) break;
                try{
                    double amount = Double.parseDouble(userInput);
                    withdrawal = new Withdrawal(-1, account.getAccountId(),amount);
                    if(((CustomerAccessCredentials) db_access).insertNewActivity(withdrawal)){
                        System.out.println("Withdrawal Successful!");
                        pauseForUser();
                    }else{
                        System.out.println("An error has occurred. Please try again");
                        pauseForUser();
                    }
                    break;
                }catch(NumberFormatException e){
                    System.out.println("Input not recognized, try again.");
                }

            }

        }
    }

    public static void doTransferMenu(){

        Transfer transfer = null;

        while(transfer == null){
            printEmptyLine();

            Account fromAccount = null;
            Account toAccount = null;
            while(fromAccount==null && toAccount==null) {
                printViewAccounts();
                System.out.println("Type the account number you would like to transfer from: ");
                getUserInput();
                if (logging_out || exit || go_back) break;
                String fromAccountStr = userInput;


                System.out.println("Type the account number you would like to transfer to: ");
                getUserInput();
                if (logging_out || exit || go_back) break;
                String toAccountStr = userInput;

                if(fromAccountStr.equals(toAccountStr)){
                    System.out.println("You cannot transfer from and to the same account.");
                    pauseForUser();
                }else{
                    fromAccount = ((CustomerAccessCredentials) db_access).getByAccountNumber(fromAccountStr);
                    if(fromAccount!=null) {
                        toAccount = ((CustomerAccessCredentials) db_access).getByAccountNumber(toAccountStr);
                        if(toAccount!=null){
                            break;
                        }else{
                            System.out.println("To account is not associated with this user. Please try again.");
                            pauseForUser();
                        }
                    }else{
                        System.out.println("From account is not associated with this user. Please try again.");
                        pauseForUser();
                    }
                    fromAccount = null;
                    toAccount = null;
                }
            }

            if (logging_out || exit || go_back) break;
            while(true){
                System.out.println("Account #"+fromAccount.getAccountNumber()+" Balance: "+((CustomerAccessCredentials) db_access).getAccountBalance(fromAccount));
                System.out.println("Account #"+toAccount.getAccountNumber()+" Balance: "+((CustomerAccessCredentials) db_access).getAccountBalance(toAccount));
                System.out.printf("How much would you like to transfer from #%s to #%s?\n", fromAccount.getAccountNumber(), toAccount.getAccountNumber());
                getUserInput();
                if (logging_out || exit || go_back) break;
                try{
                    double amount = Double.parseDouble(userInput);
                    transfer = new Transfer(-1, fromAccount.getAccountId(), toAccount.getAccountId(),amount);
                    if(((CustomerAccessCredentials) db_access).insertNewActivity(transfer)){
                        System.out.println("Transfer Successful!");
                        pauseForUser();
                    }else{
                        System.out.println("An error has occurred. Please try again");
                        pauseForUser();
                    }
                    break;
                }catch(NumberFormatException e){
                    System.out.println("Input not recognized, try again.");
                }

            }

        }
    }

    public static void printViewAccountMenu() {

        while(!exit && !logging_out){

            List<Credit> credits = ((CustomerAccessCredentials) db_access).getUserCredit();

            System.out.println("Credit lines: ");
            for(Credit credit : credits){
                double amount = credit.getLoanAmount();
                double interest = credit.getInterestRate();
                boolean pending = credit.isPending_approval();
                boolean approved = credit.isApproved();

                if(pending){
                    System.out.printf("Amount: %f  (Pending Approval)\n", amount);
                }
                else if(!approved){
                    System.out.printf("Amount: %f  (Denied)\n", amount);
                }
                else{
                    DecimalFormat dollarFormat = new DecimalFormat("#.##");
                    System.out.printf("Amount: %f  || Interest Rate: %s percent\n", amount, dollarFormat.format(interest*100));
                }

            }

            printEmptyLine();
            System.out.println("Bank Accounts: ");
            List<Account> accounts = printViewAccounts();
            printEmptyLine();

            System.out.println("Type in an account number to view transactions, or type 'back' to go back");
            printEmptyLine();

            getUserInput();
            if (logging_out || exit || go_back) break;

            List<Activity> activities;
            for(Account account : accounts){
                if(account.getAccountNumber().equals(userInput)){
                    activities = ((CustomerAccessCredentials) db_access).getAccountActivity(account);
                    printTransactionList(activities);
                }
            }

        }
    }

    public static void printTransactionList(List<Activity> activities){
        printEmptyLine();
        User user = db_access.getUser();
        for(Activity activity : activities){
            int multiplier = (activity.getFromAccountId()==user.getUserId())?-1:1;
            System.out.println(activity.getAmount()*multiplier);
        }
        pauseForUser();
    }

    public static List<Account> printViewAccounts() {

        List<Account> accounts = ((CustomerAccessCredentials) db_access).getAccounts();

        for(Account account : accounts){
            System.out.println("Account Number: "+account.getAccountNumber()+" || Account Type: "+account.getAccountDescriptor()+
                              "  ||  Balance: "+((CustomerAccessCredentials) db_access).getAccountBalance(account));
        }
        return accounts;
    }

    public static void printWelcomeMessage(){
        System.out.println("Welcome, "+current_user.getFistName()+" "+current_user.getLastName());
        System.out.println("What would you like to do?");
        System.out.println();
    }

    public static void printEmployeeSelection(){
        System.out.println(MenuOption.CREATE_USER.menuCode+": create new user account");
        System.out.println(MenuOption.OPEN_ACCOUNT.menuCode+": create accounts for bank users");
        System.out.println(MenuOption.APPROVE_CREDIT.menuCode+": review credit requests");
        System.out.println();
        System.out.println("Type 'logout' to log out of your current session");

        MenuOption select = null ;
        while(select == null && !exit && !logging_out){

            setBackPoint();

            getUserInput();
            if (logging_out || exit) return;
            select = MenuOption.getEmployeActivityMenuOption(userInput);
        }

        if(select==MenuOption.OPEN_ACCOUNT){
            printCreateAccountMenu();
        }else if(select==MenuOption.APPROVE_CREDIT){
            printApproveCreditMenu();
        }else if(select==MenuOption.CREATE_USER){
            printCreateUser();
        }
    }

    public static void printCreateUser(){

    }

    public static void printApproveCreditMenu(){
        l1: while(true){
                User user;
                while(true){
                    System.out.println("Type in username of user to look up their requests:");
                    getUserInput();
                    if(exit||logging_out||go_back) break l1;
                    user = ((EmployeeAccessCredentials)db_access).getUserByUsername(userInput);
                    if(user!=null) break;

                    System.out.println("Could not find user, please try again");
                    pauseForUser();
                }

                List<Credit> allcredits = ((EmployeeAccessCredentials)db_access).getUserCredit(user);
                List<Credit> pendingcredits = new ArrayList<Credit>();
                //Get rid of non-pending approvals
                for(Credit credit : allcredits){
                    if(credit.isPending_approval()){
                        pendingcredits.add(credit);
                    }
                }

                Credit creditToApprove;
                while(true){

                    if(pendingcredits.size()==0) {
                        System.out.println("This user has no pending credit approvals");
                        pauseForUser();
                    }else{
                        int i = 1;
                        for(Credit credit : pendingcredits){
                            System.out.println(i+":   amount: "+credit.getLoanAmount()+" || interest: %"+credit.getInterestRate()*100);
                            i++;
                        }
                    }
                    printEmptyLine();
                    System.out.println("Type in a number to approve/deny a request:");
                    getUserInput();
                    if(exit||logging_out||go_back) break l1;

                    try{
                        int index = Integer.parseInt(userInput)-1;
                        creditToApprove = pendingcredits.get(index);
                        break;

                    }catch(NumberFormatException | IndexOutOfBoundsException e){
                        System.out.println("Could not understand that input. Please try again");
                        pauseForUser();
                    }
                }

                System.out.println("Would you like to approve this credit line?");
                System.out.println("Type 'y' to approve, 'n' to deny, or 'back' to go back");
                boolean approve = getUserYesOrNo();
                if(exit||logging_out||go_back) break;


            ((EmployeeAccessCredentials)db_access).approveCredit(creditToApprove, approve);
            System.out.println("Update Successful.");
            pauseForUser();
            break;


            }


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
            printEmptyLine();
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

            printEmptyLine();
            System.out.println("Account Details:");
            System.out.println("Account Holder Name: "+ userToMakeAccountFor.getFistName()+" "+userToMakeAccountFor.getLastName());
            System.out.println("Account Number: "+accountToInsert.getAccountNumber());
            System.out.println("Account Type: "+accountToInsert.getAccountDescriptor());
            printEmptyLine();
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
                //db_access = FileLogin.login(user,pass);
                break;
            case("SQL"):
                db_access = SQLLogin.login(user, pass);
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
        db_access = null;
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
        printEmptyLine();
        System.out.println("Press 'Enter' to continue...");
        in.nextLine();
    }

    private static void printEmptyLine() {
        System.out.println();
    }


    private static boolean getUserYesOrNo(){
        userInput = "";
        while( !userInput.equals("y") && !userInput.equals("n") && !go_back){
            System.out.print("(y/n): ");
            getUserInput();
        }
        return userInput.equals("y");
    }

    private enum MenuOption{
        // Employee Menu Options
        CREATE_USER("1"), OPEN_ACCOUNT("2"), APPROVE_CREDIT("3"),

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

        public static MenuOption getEmployeActivityMenuOption(String code){
            if(code.equals(OPEN_ACCOUNT.menuCode)){
                return OPEN_ACCOUNT;
            }else if(code.equals(APPROVE_CREDIT.menuCode)){
                return APPROVE_CREDIT;
            }else if(code.equals(CREATE_USER.menuCode)){
                return CREATE_USER;
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
