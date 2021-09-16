package org.project.dataaccess.credentials;

import org.project.beans.User;
import org.project.dataaccess.AccessCredentialsObject;

public class UserAccessCredentials extends AccessCredentialsObject {

    private User userLoggedIn;

    private UserAccessCredentials(User user){
        super();
        userLoggedIn=user;
    }

    public User getUser(){
        return userLoggedIn;
    }

    public static AccessCredentialsObject newInstance(User user) throws AccessError {
        if(singleton==null){
            singleton = new UserAccessCredentials(user);

            return AccessCredentialsObject.singleton;
        }else{
            throw new AccessError("Credential Object Already Exists");
        }
    }


    @Override
    public String toString() {
        return "UserAccessCredentials{" +
                "userLoggedIn=" + userLoggedIn +
                '}';
    }
}
