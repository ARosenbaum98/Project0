package org.project.dataaccess;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.project.beans.Account;
import org.project.beans.Credit;
import org.project.beans.User;
import org.project.beans.activities.*;
import org.project.dataaccess.credentials.CustomerAccessCredentials;
import org.project.dataaccess.credentials.EmployeeAccessCredentials;

import java.io.IOException;

import org.postgresql.Driver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.sql.JDBCType.*;

class SQLBankConnection implements BankConnection {

    private static final Logger errorLog = Logger.getLogger(BankConnection.class);

    private static final String username;
    private static final String password;
    private static final String url;

    private static final SQLBankConnection singleton = new SQLBankConnection();

    private static final Properties properties = new Properties();

    static {
        try {
            // Read db properties
            properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream("db.properties"));

            // Load class from file
            Class<?> cl = Class.forName(properties.getProperty("db.driver_name"));

            // Create instance of driver class
            Driver driver = (Driver) cl.newInstance();

            //Register driver
            DriverManager.registerDriver(driver);


        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("db.properties file not found");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("db.properties unable to parse driver details");
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException("db.properties driver class either does not exist or is not configured properly");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("DB driver marked as private");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException("Unable to register driver");
        }

        username = properties.getProperty("db.username");
        password = properties.getProperty("db.password");
        url = properties.getProperty("db.url");
    }

    private SQLBankConnection(){};

    static SQLBankConnection getInstance() {
        return SQLBankConnection.singleton;
    }

    @Override
    public AccessCredentialsObject login(String user, String pass) {

        String login_sql = "SELECT * FROM bank_user WHERE username = ? AND user_password = ?";
        JDBCType[] types = {VARCHAR, VARCHAR};
        String[] wildcards = {user, pass};

        ResultSet results = executePreparedSelectStatement(login_sql,types, wildcards);

        User userObj = null;

        try{
            while (results.next()) {
                userObj = buildUserFromResultSet(results);

                if (userObj.isEmployee()) {
                    try {
                        errorLog.log(Priority.INFO, "Employee Login: "+userObj.getUsername());
                        return EmployeeAccessCredentials.newInstance(userObj);
                    } catch (AccessCredentialsObject.AccessError e) {
                        e.printStackTrace();
                        throw new RuntimeException(e.getMessage());
                    }
                }else{
                    try {
                        errorLog.log(Priority.INFO, "Customer Login: "+userObj.getUsername());
                        return CustomerAccessCredentials.newInstance(userObj);
                    }catch(AccessCredentialsObject.AccessError e){
                        e.printStackTrace();
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Unable to log in at this time");
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM bank_user";

        ResultSet results = executeSelectStatement(sql);

        List<User> users = new ArrayList<>();

        try{
            while(results.next()){
                users.add(buildUserFromResultSet(results));
            }
            return users;
        }catch (SQLException e){
            throw new RuntimeException("Error while parsing all users");
        }
    }

    @Override
    public List<Account> getAllAccounts() {
        String sql = "SELECT * FROM bank_account";

        ResultSet results = executeSelectStatement(sql);

        List<Account> accounts = new ArrayList<>();

        try{
            while(results.next()){
                accounts.add(buildAccountFromResultSet(results));
            }
            return accounts;
        }catch (SQLException e){
            throw new RuntimeException("Error while parsing all accounts");
        }
    }

    @Override
    public List<Activity> getAllActivities() {
        String sql = "SELECT * FROM bank_activity";

        ResultSet results = executeSelectStatement(sql);

        List<Activity> activities = new ArrayList<>();

        try{
            while(results.next()){
                activities.add(buildActivityFromResultSet(results));
            }
            return activities;
        }catch (SQLException e){
            throw new RuntimeException("Error while parsing all activities");
        }
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT * " +
                     "FROM bank_user WHERE id = ?";
        ResultSet results = executePreparedSelectStatement(sql, INTEGER, String.valueOf(id));

        User user = null;
        try {
            while (results.next()) {
                user = buildUserFromResultSet(results);
            }
        }catch (SQLException e){
            throw new RuntimeException("Error fetching user");
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        String sql = "SELECT * " +
                     "FROM bank_user WHERE username = ?";
        ResultSet results = executePreparedSelectStatement(sql, VARCHAR, username);

        User user = null;
        try {
            while (results.next()) {
                user = buildUserFromResultSet(results);
            }
        }catch (SQLException e){
            throw new RuntimeException("Error fetching user");
        }
        return user;
    }

    @Override
    public Account getAccountById(int id) {
        String sql = "SELECT * " +
                "FROM bank_account WHERE id = ?";
        ResultSet results = executePreparedSelectStatement(sql, INTEGER, Integer.toString(id));

        Account account = null;
        try {
            while (results.next()) {
                account = buildAccountFromResultSet(results);
            }
        }catch (SQLException e){
            throw new RuntimeException("Error fetching account");
        }
        return account;
    }

    @Override
    public Account getByAccountNumber(String id) {
        String sql = "SELECT * " +
                "FROM bank_account WHERE account_number = ?";
        ResultSet results = executePreparedSelectStatement(sql, VARCHAR, id);

        Account account = null;
        try {
            while (results.next()) {
                account = buildAccountFromResultSet(results);
            }
        }catch (SQLException e){
            throw new RuntimeException("Error fetching account");
        }
        return account;
    }

    @Override
    public Activity getActivityById(int id) {
        return null;
    }

    @Override
    public List<Account> getUserAccounts(User user) {
        List<Account> accounts = new ArrayList<>();

        String sql = "SELECT * FROM bank_account WHERE user_id = ?";
        JDBCType type = INTEGER;
        String arg = String.valueOf(user.getUserId());

        ResultSet results = executePreparedSelectStatement(sql, type, arg);

        try{
            while(results.next()){
                Account account = new Account(results.getInt("id"),
                                              results.getInt("user_id"),
                                              results.getString("account_number"),
                                              results.getString("account_type"));
                accounts.add(account);
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException("Could not get accounts from user "+user.toString());
        }

        return accounts;
    }

    @Override
    public List<Activity> getAccountActivity(Account account) {
        List<Activity> activities = new ArrayList<>();

        String sql = "SELECT * FROM bank_activity WHERE from_account_id = ? OR to_account_id = ?";
        JDBCType[] types = {INTEGER, INTEGER};
        String[] args ={String.valueOf(account.getAccountId()),String.valueOf(account.getAccountId()),};

        ResultSet results = executePreparedSelectStatement(sql, types, args);

        try{
            while(results.next()){
                Activity activity = null;

                int id = results.getInt("id");
                int from_account_id = results.getInt("from_account_id");
                int to_account_id = results.getInt("to_account_id");
                int type = results.getInt("activity_type");
                double amount = results.getDouble("amount");

                if(type==ActivityType.DEPOSIT.getDatabaseCode()){
                    activity = new Deposit(id,to_account_id,amount);
                }else  if(type==ActivityType.WITHDRAWAL.getDatabaseCode()){
                    activity = new Withdrawal(id,from_account_id,amount);
                }else  if(type==ActivityType.TRANSFER.getDatabaseCode()){
                    activity = new Transfer(id,from_account_id,to_account_id,amount);
                }
                activities.add(activity);
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException("Could not get activity from account "+account.toString());
        }

        return activities;

    }

    @Override
    public boolean insertNewUser(User user) {
        String sql = "insert into bank_user (username, user_password, is_employee, fname, lname) " +
                "values(?,?,?,?,?)";
        JDBCType[] types = {VARCHAR, VARCHAR, BOOLEAN, VARCHAR, VARCHAR};
        String[] args    = {user.getUsername(),
                            user.getPassword(),
                            String.valueOf(user.isEmployee()),
                            user.getFistName(),
                            user.getLastName()};

         executePreparedUpdateStatement(sql, types, args);
        errorLog.log(Priority.INFO, "New user inserted (Id: "+user.getUserId()+" Username: "+user.getUsername()+")");
        return true;
    }

    @Override
    public boolean insertNewAccount(Account account) {
        String sql = "insert into bank_account " +
                "(user_id, account_number, account_type) " +
                "values(?,?,?)";
        JDBCType[] types = {INTEGER, VARCHAR, VARCHAR};
        String[] args    = {String.valueOf(account.getUserId()),
                            account.getAccountNumber(),
                            account.getAccountDescriptor()
        };

        executePreparedUpdateStatement(sql, types, args);
        errorLog.log(Priority.INFO, "New user inserted (Id: "+account.getUserId()+" Account Number: "+account.getAccountNumber()+")");
        return true;
    }

    @Override
    public boolean insertNewActivity(Activity activity) {
        int type = -1;

        if(activity instanceof Deposit){
            type = ActivityType.DEPOSIT.getDatabaseCode();
        }else if(activity instanceof Withdrawal){
            type = ActivityType.WITHDRAWAL.getDatabaseCode();
        }else if(activity instanceof Transfer){
            type = ActivityType.TRANSFER.getDatabaseCode();
        }

        String sql = "insert into bank_activity " +
                "(from_account_id, to_account_id, activity_type, amount) " +
                "values(?,?,?,?)";
        JDBCType[] types = {INTEGER,INTEGER,INTEGER,DOUBLE};
        String[] args    = {(activity.getFromAccountId()!=-1)?String.valueOf(activity.getFromAccountId()):null,
                            (activity.getToAccountId()!=-1)?String.valueOf(activity.getToAccountId()): null,
                            String.valueOf(type),
                            String.valueOf(activity.getAmount())};

        executePreparedUpdateStatement(sql, types, args);
        errorLog.log(Priority.INFO, "New activity inserted (Id: "+activity.getActivityId()+")");
        return true;
    }

    @Override
    public boolean insertNewCredit(Credit credit) {
        String sql = "insert into bank_credit_accounts (user_id, loan_amount, interest_rate, pending_approval, approved) " +
                "values(?,?,?,?,?)";
        JDBCType[] types = {INTEGER, FLOAT, FLOAT, BOOLEAN, BOOLEAN};
        String[] args    = {
                String.valueOf(credit.getUserId()),
                String.valueOf(credit.getLoanAmount()),
                String.valueOf(credit.getInterestRate()),
                String.valueOf(credit.isPending_approval()),
                String.valueOf(credit.isApproved() )

        };

        executePreparedUpdateStatement(sql, types, args);
        errorLog.log(Priority.INFO, "New credit request inserted (Id: "+credit.getId()+" Auto-Approved: "+credit.isApproved()+")");
        return true;
    }

    @Override
    public Credit getCreditById(int id) {
        return null;
    }

    @Override
    public List<Credit> getUserCredit(User user) {
        List<Credit> credits = new ArrayList<>();

        String sql = "SELECT * FROM bank_credit_accounts WHERE user_id = ?";
        JDBCType type = INTEGER;
        String arg = String.valueOf(user.getUserId());

        ResultSet results = executePreparedSelectStatement(sql, type, arg);

        try{
            while(results.next()){
                Credit credit = new Credit(results.getInt("id"),
                        results.getInt("user_id"),
                        results.getDouble("loan_amount"),
                        results.getDouble("interest_rate"),
                        results.getBoolean("pending_approval"),
                        results.getBoolean("approved"));
                credits.add(credit);
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException("Could not get accounts from user "+user.toString());
        }

        return credits;
    }

    public void approveCredit(Credit credit, boolean approve) {
        String sql = "UPDATE bank_credit_accounts "+
                     "SET pending_approval = false, "+
                     "approved = ? "+
                     "WHERE id = ?";
        JDBCType[] types = {BOOLEAN, INTEGER};
        String[] args = {String.valueOf(approve),
                         String.valueOf(credit.getId())};

        executePreparedUpdateStatement(sql, types, args);
    }


    private static Connection newConnection(String user, String pass, String url) throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    private static Connection newConnection() throws SQLException {
        return newConnection(username, password, url);
    }

    private static void assignWildCards(PreparedStatement ps, JDBCType[] types, String[] args) throws SQLException {

        if (types.length != args.length){
            throw new RuntimeException("Length of wildcard types does not match number of args");
        }

        // Assign wildcards
        int i = 0;
        for(String arg : args){if(types[i]==VARCHAR){
                if(arg!=null)
                    ps.setString(i+1, arg);
                else
                    ps.setNull(i+1, Types.VARCHAR);
            }else if(types[i]==INTEGER){
                if(arg!=null)
                    ps.setInt(i+1, Integer.parseInt(arg));
                else
                    ps.setNull(i+1, Types.INTEGER);
            }else if(types[i]==DOUBLE){
                if(arg!=null)
                    ps.setDouble(i+1, Double.parseDouble(arg));
                else
                    ps.setNull(i+1, Types.DOUBLE);
            }else if(types[i]==BOOLEAN){
                if(arg!=null)
                    ps.setBoolean(i+1, Boolean.parseBoolean(arg));
                else
                    ps.setNull(i+1, Types.BOOLEAN);
            }else if(types[i]==FLOAT){
                if(arg!=null)
                    ps.setFloat(i+1, Float.parseFloat(arg));
                else
                    ps.setNull(i+1, Types.FLOAT);
            }else{
                throw new RuntimeException("Could not parse wildcard type");
            }
            i++;
        }

    }

    private void executePreparedUpdateStatement(String sql, JDBCType type, String arg){
        executePreparedUpdateStatement(sql, new JDBCType[]{type}, new String[]{arg});
    }

    private void executePreparedUpdateStatement(String sql, JDBCType[] types, String[] args){
        executePreparedUpdateStatement(sql, types, args, Connection.TRANSACTION_READ_UNCOMMITTED);
    }

    private void executePreparedUpdateStatement(String sql, JDBCType type, String arg, int transactionLevel){
        executePreparedUpdateStatement(sql, new JDBCType[] {type}, new String[] {arg}, transactionLevel);
    }

    private void executePreparedUpdateStatement(String sql, JDBCType[] types, String[] args, int transactionLevel) {
        try (Connection c = newConnection()) {

            // Create statement object
            PreparedStatement ps = c.prepareStatement(sql);

            // Assign wildcards
            assignWildCards(ps, types, args);

            // Get db metadata
            DatabaseMetaData metaData = c.getMetaData();

            if(metaData.supportsTransactionIsolationLevel(transactionLevel))
            {
                c.setTransactionIsolation(transactionLevel);

                // execute sql statement
                int insertedCount = ps.executeUpdate();

            }else{
                throw new RuntimeException("Serialization Level prohibited. Insert aborted");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException("SQL insert failed");
        }
    }

    private static ResultSet executePreparedSelectStatement(String sql, JDBCType[] types, String[] args){
        return executePreparedSelectStatement(sql, types, args, Connection.TRANSACTION_READ_UNCOMMITTED);
    }

    private static ResultSet executePreparedSelectStatement(String sql, JDBCType type, String arg) {
        return executePreparedSelectStatement(sql, new JDBCType[]{type}, new String[]{arg});
    }

    private static ResultSet executePreparedSelectStatement(String sql, JDBCType types, String args, int transactionLevel){
        return executePreparedSelectStatement(sql, new JDBCType[]{types}, new String[]{args}, transactionLevel);
    }

    private static ResultSet executePreparedSelectStatement(String sql, JDBCType[] types, String[] args, int transactionLevel){

        try(Connection c = newConnection()){

            // Create prepared statement
            PreparedStatement ps = c.prepareStatement(sql);
            assignWildCards(ps, types, args);

            // Set transation level
            DatabaseMetaData metaData = c.getMetaData();
            if(metaData.supportsTransactionIsolationLevel(transactionLevel))
            {
                c.setTransactionIsolation(transactionLevel);
            }else{
                throw new RuntimeException("Serialization Level prohibited. Insert aborted");
            }

            // execute sql statement
            ResultSet results = ps.executeQuery();
            return results;

        }catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException("Could not connect to database.");
        }
    }

    private static ResultSet executeSelectStatement(String sql){
        try(Connection c = newConnection()){

            Statement s = c.createStatement();
            ResultSet results = s.executeQuery(sql);
            return results;

        }catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException("Could not connect to database.");
        }
    }

    private static User buildUserFromResultSet(ResultSet results) throws SQLException {
        int id = results.getInt("id");
        String username = results.getString("username");
        String password = results.getString("user_password");
        boolean isEmployee = results.getBoolean("is_employee");
        String fname = results.getString("fname");
        String lname = results.getString("lname");

        return new User(id, username, password, fname, lname, isEmployee);
    }

    private static Account buildAccountFromResultSet(ResultSet results) throws SQLException {
        int id = results.getInt("id");
        int user_id = results.getInt("user_id");
        String account_number = results.getString("account_number");
        String account_type = results.getString("account_type");

        return new Account(id, user_id, account_number, account_type);
    }

    private static Activity buildActivityFromResultSet(ResultSet results) throws SQLException {
        int id = results.getInt("id");
        int from_account_id = results.getInt("from_account_id");
        int to_account_id = results.getInt("to_account_id");
        int activity_type = results.getInt("activity_type");
        double amount = results.getDouble("amount");

        if(activity_type==ActivityType.DEPOSIT.getDatabaseCode()){
            return new Deposit(id, to_account_id, amount);
        }else if(activity_type==ActivityType.WITHDRAWAL.getDatabaseCode()){
            return new Withdrawal(id, from_account_id, amount);
        }else if(activity_type==ActivityType.TRANSFER.getDatabaseCode()){
            return new Transfer(id, from_account_id, to_account_id, amount);
        }
        return null;
    }

}
