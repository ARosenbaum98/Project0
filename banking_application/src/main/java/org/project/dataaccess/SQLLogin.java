package org.project.dataaccess;
import org.project.beans.User;
import org.project.dataaccess.credentials.EmployeeAccessCredentials;
import org.project.dataaccess.credentials.FullAccessCredentials;

import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SQLLogin {

    public static AccessCredentialsObject login(String username, String password) {

        // Try to connect to database
        try {
            AccessCredentialsObject.connectToDatabase(SQLBankConnection.getInstance());
        } catch (AccessCredentialsObject.AccessError e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot connect to database");
        }

        // Get access level from account
        AccessCredentialsObject access = SQLBankConnection.getInstance().login(username, password);

        // If login fails, disconnect from the database
        if(access==null){
            AccessCredentialsObject.disconnectFromDatabase();
        }

        // Return access object
        return access;
    }

    public static AccessCredentialsObject login_test(){

        AccessCredentialsObject.disconnectFromDatabase();

        try {
            AccessCredentialsObject.connectToDatabase(SQLBankConnection.getInstance());
            return FullAccessCredentials.newInstance(new User(-1,"testuser","test","test","user",true));
        } catch (AccessCredentialsObject.AccessError e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
