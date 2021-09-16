package org.project.beans;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class User {
    private int userId;
    private String username;
    private String password;
    private String fname;
    private String lname;
    private boolean isEmployee;


    private static final Logger errorLog = Logger.getLogger(User.class);

    /**
     *
     * @param userId int - user's unique database ID
     * @param username String - user's username
     * @param password String - user's password
     * @param fname String - user's first name
     * @param lname String - user's last name
     * @param isEmployee boolean - true if the user is an employee, false otherwise
     */
    public User(int userId, String username, String password, String fname, String lname, boolean isEmployee) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
        this.isEmployee = isEmployee;
    }

    /**
     * @param parameters a list of strings containing user data
     */
    public User(List<String> parameters){
        try{
            int userId = Integer.parseInt(parameters.get(0));
            String username = parameters.get(1);
            String password = parameters.get(2);
            String fname = parameters.get(3);
            String lname = parameters.get(4);
            boolean isEmployee = (parameters.get(5).equals("1"));

            this.userId = userId;
            this.username = username;
            this.password = password;
            this.fname = fname;
            this.lname = lname;
            this.isEmployee = isEmployee;


        }catch(ArrayIndexOutOfBoundsException e){
            errorLog.error("User parameters string is too short: "+e.getMessage());
            e.printStackTrace();
        }catch(NullPointerException e){
            errorLog.error("Cannot parse values from user parameter list: "+e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param parameters - String
     */
    public User(String parameters){
        this(Arrays.asList(parameters.split(",")));
    }

    /**
     * @return int - returns user's ID number
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @return String - returns user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return String - returns user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return String - returns user's first name
     */
    public String getFistName() {
        return fname;
    }

    /**
     * @return String - returns user's last name
     */
    public String getLastName() {
        return lname;
    }

    /**
     * @return Boolean - returns true if user is an employee
     */
    public boolean isEmployee() {
        return isEmployee;
    }

    /**
     * @return String returns CSV file-ready string
     */
    public String stringify(){
        return ""+userId+","+username+","+password+","+fname+","+lname+","+((isEmployee)?"1":"0");
    }

    @Override
    public String toString() {
        return "User{" +
                "accountId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", isEmployee=" + isEmployee +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId && isEmployee == user.isEmployee && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(fname, user.fname) && Objects.equals(lname, user.lname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, password, fname, lname, isEmployee);
    }
}
