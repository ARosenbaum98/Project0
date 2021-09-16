package org.project.beans.activities;

import java.util.Arrays;
import java.util.List;

public class ActivityFactory {

    /**
     * @param data - List<String>
     * @return - Returns either WITHDRAWAL, DEPOSIT, or TRANSFER based on the data type
     */
    public static Activity getActivity(List<String> data){

        ActivityType type = ActivityType.dbCodeToActivityType(Integer.parseInt(data.get(4)));

        if(type==ActivityType.WITHDRAWAL){
            return new Withdrawal(data);
        }else if(type==ActivityType.DEPOSIT) {
            return new Deposit(data);
        }else if(type==ActivityType.TRANSFER){
            return new Transfer(data);

        }else{
            throw new ActivityNotFoundException();
        }
    }

    public static Activity getActivity(String data){
        return getActivity(Arrays.asList(data.split(",")));

    }


    public static final class ActivityNotFoundException extends RuntimeException{}
}
