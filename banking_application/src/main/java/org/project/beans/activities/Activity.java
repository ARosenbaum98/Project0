package org.project.beans.activities;

public interface Activity {
    int getActivityId();
    int getFromAccountId();
    int getToAccountId();
    double getAmount();

    boolean isAssociatedAccountId(int accountId);

    /**
     * @return String returns CSV file-ready string
     */
    default String stringify(){
        return ""+getActivityId()+","+
                ((getFromAccountId()==-1)?"":getFromAccountId())+","+
                ((getToAccountId()==-1)?"":getToAccountId())+","
                +getAmount();
    }
}
