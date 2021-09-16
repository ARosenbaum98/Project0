package org.project.beans.activities;

public enum ActivityType {

    NONE(-1),
    DEPOSIT(1),
    WITHDRAWAL(2),
    TRANSFER(3);

    private final int databaseCode;

    private ActivityType(int databaseCode){
        this.databaseCode = databaseCode;
    }

    public final int getDatabaseCode() {
        return databaseCode;
    }

    public static final ActivityType dbCodeToActivityType(int code){
        switch(code){
            case(1):
                return DEPOSIT;
            case(2):
                return WITHDRAWAL;
            case(3):
                return TRANSFER;
            case(-1):
                return NONE;
            default:
                return null;
        }

    }
}
