package org.project.beans;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Account {

    private int accountId;
    private int userId;
    private String accountNumber;
    private String accountDescriptor;

    private static final Logger errorLog = Logger.getLogger(User.class);

    /**
     * @param accountId - int
     * @param userId - int
     * @param accountNumber - String
     * @param accountDescriptor -String
     */
    public Account(int accountId, int userId, String accountNumber, String accountDescriptor) {
        this.accountId = accountId;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.accountDescriptor = accountDescriptor;
    }

    /**
     * @param parameters - List<String>
     */
    public Account(List<String> parameters){
        try{
            int accountId = Integer.parseInt(parameters.get(0));
            int userId = Integer.parseInt(parameters.get(1));
            String accountNumber = parameters.get(2);
            String accountDescriptor = parameters.get(3);

            this.accountId = accountId;
            this.userId = userId;
            this.accountNumber = accountNumber;
            this.accountDescriptor = accountDescriptor;


        }catch(ArrayIndexOutOfBoundsException e){
            errorLog.error("Account parameters string is too short: "+e.getMessage());
            e.printStackTrace();
        }catch(NullPointerException e){
            errorLog.error("Cannot parse values from account parameter list: "+e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * @param parameters - String
     */
    public Account(String parameters){
        this(Arrays.asList(parameters.split(",")));
    }

    public int getAccountId() {
        return accountId;
    }

    public int getUserId() {
        return userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountDescriptor() {
        return accountDescriptor;
    }

    /**
     * @return String returns CSV file-ready string
     */
    public String stringify(){
        return ""+accountId+","+userId+","+accountNumber+","+accountDescriptor;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", userId=" + userId +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountDescriptor='" + accountDescriptor + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId && userId == account.userId && Objects.equals(accountNumber, account.accountNumber) && Objects.equals(accountDescriptor, account.accountDescriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, userId, accountNumber, accountDescriptor);
    }
}
