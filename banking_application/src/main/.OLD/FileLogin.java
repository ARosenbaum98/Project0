package org.project.dataaccess;

public class FileLogin {

    /**
     * @param username String
     * @param password String
     * @return Returns AccessCredentialsObject object if login is successful. If not successful, returns null.
     */
    public static AccessCredentialsObject login(String username, String password) {

        // Try to connect to database
        try {
            AccessCredentialsObject.connectToDatabase(FileBankConnection.getInstance());
        } catch (AccessCredentialsObject.AccessError e) {
            e.printStackTrace();
        }

        // Get access level from account
        AccessCredentialsObject access = FileBankConnection.getInstance().login(username, password);

        // If login fails, disconnect from the database
        if(access==null){
            AccessCredentialsObject.disconnectFromDatabase();
        }

        // Return access object
        return access;
    }
}
