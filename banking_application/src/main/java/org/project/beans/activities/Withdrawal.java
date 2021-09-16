package org.project.beans.activities;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class Withdrawal extends SingleAccountActivity {


    private static final Logger errorLog = Logger.getLogger(Withdrawal.class);

    /**
     * @param activityId - int
     * @param fromAccount - int
     * @param amount - double
     */
    public Withdrawal(int activityId, int fromAccount, double amount) {
        super(activityId, amount);
        this.fromAccountId = fromAccount;
        this.toAccountId = -1;
    }

    public Withdrawal(List<String> parameters) {
        super(parameters);


        int fromAccountId = Integer.parseInt(parameters.get(1));
        int toAccountId = -1;

        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
    }

    public Withdrawal(String parameters) {
        this(Arrays.asList(parameters.split(",")));
    }

    @Override
    public String stringify(){
        return super.stringify()+","+ActivityType.WITHDRAWAL.getDatabaseCode();
    }

    @Override
    public String toString() {
        return "Withdrawal{" +
                "activityId=" + activityId +
                ", fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                ", amount=" + amount +
                '}';
    }
}
