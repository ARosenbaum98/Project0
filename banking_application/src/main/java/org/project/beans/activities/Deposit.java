package org.project.beans.activities;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class Deposit extends SingleAccountActivity {

    private static final Logger errorLog = Logger.getLogger(Withdrawal.class);

    /**
     * @param activityId - int
     * @param toAccount - int
     * @param amount - double
     */
    public Deposit(int activityId, int toAccount, double amount) {
        super(activityId, amount);

        this.toAccountId = toAccount;
        this.fromAccountId = -1;
    }

    public Deposit(List<String> parameters) {
        super(parameters);

        int toAccountId = Integer.parseInt(parameters.get(2));
        int fromAccountId = -1;

        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
    }


    public Deposit(String parameters) {
        this(Arrays.asList(parameters.split(",")));
    }

    @Override
    public String stringify(){
        return super.stringify()+","+ActivityType.DEPOSIT.getDatabaseCode();
    }

    @Override
    public String toString() {
        return "Deposit{" +
                "activityId=" + activityId +
                ", fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                ", amount=" + amount +
                '}';
    }
}
