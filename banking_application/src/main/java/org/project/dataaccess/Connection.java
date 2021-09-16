package org.project.dataaccess;

import org.project.beans.Account;
import org.project.beans.User;
import org.project.beans.activities.Activity;

import java.util.List;

public interface Connection {

    public AccessCredentialsObject login(String username, String password);

    public List<User> getAllUsers();

    public List<Account> getAllAccounts() ;

    public List<Activity> getAllActivities();

    public User getUserById(int id);

    public Account getAccountById(int id);

    public Activity getActivityById(int id);

    public List<Account> getUserAccounts(User user);

    public List<Activity> getAccountActivity(Account account) ;

    public boolean insertNewUser(User user) ;

    public boolean insertNewAccount(Account account);

    public boolean insertNewActivity(Activity activity);

    User getUserByUsername(String username);
}
