package org.project.beans.activities;

import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class SingleAccountActivity implements Activity{
    int activityId;
    int fromAccountId;
    int toAccountId;
    double amount;

    private static final DecimalFormat dollarFormat = new DecimalFormat("#.##");

    private static final Logger errorLog = Logger.getLogger(Deposit.class);

    public SingleAccountActivity(int activityId, double amount) {
        this.activityId = activityId;
        this.amount = amount;
    }

    /**
     * @param parameters - List<String>
     */
    public SingleAccountActivity(List<String> parameters){

        try{
            int activityId = Integer.parseInt(parameters.get(0));
            double amount = Double.parseDouble(parameters.get(3));

            this.activityId = activityId;
            this.amount = amount;

        }catch(ArrayIndexOutOfBoundsException e){
            errorLog.error("User parameters string is too short: "+e.getMessage());
            e.printStackTrace();
        }catch(NullPointerException e){
            errorLog.error("Cannot parse values from user parameter list: "+e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public int getActivityId() {
        return this.activityId;
    }

    @Override
    public int getFromAccountId() {
        return this.fromAccountId;
    }

    @Override
    public int getToAccountId() {
        return this.toAccountId;
    }

    @Override
    public double getAmount() {
        return Double.parseDouble(dollarFormat.format(amount));
    }

    @Override
    public boolean isAssociatedAccountId(int accountId){
        return this.fromAccountId == accountId || this.toAccountId == accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleAccountActivity that = (SingleAccountActivity) o;
        return activityId == that.activityId && fromAccountId == that.fromAccountId && toAccountId == that.toAccountId && Double.compare(that.amount, amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId, fromAccountId, toAccountId, amount);
    }
}
