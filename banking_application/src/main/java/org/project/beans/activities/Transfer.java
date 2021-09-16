package org.project.beans.activities;

import java.util.Arrays;
import java.util.List;

public class Transfer extends SingleAccountActivity{

    /**
     * @param activityId
     * @param fromAccountId
     * @param toAccountId
     * @param amount
     */
    public Transfer(int activityId, int fromAccountId, int toAccountId, double amount) {
        super(activityId, amount);
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
    }

    public Transfer(List<String> parameters) {
        super(parameters);

        int toAccountId = Integer.parseInt(parameters.get(2));
        int fromAccountId = Integer.parseInt(parameters.get(1));

        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
    }

    public Transfer(String parameters) {
        this(Arrays.asList(parameters.split(",")));
    }

    @Override
    public String stringify(){
        return super.stringify()+","+ActivityType.TRANSFER.getDatabaseCode();
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "activityId=" + activityId +
                ", fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                ", amount=" + amount +
                '}';
    }
}
