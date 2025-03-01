package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import Model.Account;
import Util.ConnectionUtil;

public class JDBCAccountDao implements AccountDao{

    //Note: If an SQL exception is thrown, an empty Optional object is returned. 
    @Override
    public Optional<Account> insertAccount(Account newAccount) {

        Connection connection = ConnectionUtil.getConnection();
        String insertSQL = "INSERT INTO account (username, password) VALUES (?, ?);";
        try {
            PreparedStatement ps = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, newAccount.getUsername());
            ps.setString(2, newAccount.getPassword());

             ps.executeUpdate();
            
            
            ResultSet pkeyResultSet = ps.getGeneratedKeys();
            if(pkeyResultSet.next()){
                int generatedAccountId = pkeyResultSet.getInt(1);
                return Optional.of(new Account(generatedAccountId, newAccount.getUsername(), newAccount.getPassword()));
            }
        } catch (SQLException e) {
            //Just print the exception to the console
            e.printStackTrace();
            if(e.getSQLState().equals("23000")){
                //Should happen in the case the account attempting to insert already exists.
                return Optional.empty();
            }
            //In the case of a database access error, throw a runtime exception
            throw new RuntimeException(e);
        }
        //Should happen in the case the account attempting to insert already exists.
        return Optional.empty();
       
    }
    
    @Override
    public Optional<Account> verifyLoginInformation(String username, String password) {
      
        Connection connection = ConnectionUtil.getConnection();
        String selectSQL = "SELECT account_id, username, password FROM account WHERE username = ? AND password = ?;";
        try {
            PreparedStatement ps = connection.prepareStatement(selectSQL);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return Optional.of(mapRowToAccount(rs));
            }
            
        } catch (SQLException e) {
            //Just print the exception to the console
            e.printStackTrace();
            throw new RuntimeException(e);

        }
        //In the case the account was not found
        return Optional.empty();
       
    }

    @Override
    public Optional<Account> getAccountById(int id) {
        Connection connection = ConnectionUtil.getConnection();
        String selectSQL = "SELECT account_id, username, password FROM account WHERE account_id = ?;";
        try {
            PreparedStatement ps = connection.prepareStatement(selectSQL);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return Optional.of(mapRowToAccount(rs));
            }
            
        } catch (SQLException e) {
            //just print the exception to the console
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        //In the case the account was not found
        return Optional.empty();
       
    }

    Account mapRowToAccount(ResultSet rs) throws SQLException{
        int account_id = rs.getInt("account_id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        return new Account(account_id, username, password);
    }

    @Override
    public Optional<Account> getAccountByUsername(String username) {
        Connection connection = ConnectionUtil.getConnection();
        String selectSQL = "SELECT account_id, username, password FROM account WHERE username = ?;";
        try {
            PreparedStatement ps = connection.prepareStatement(selectSQL);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return Optional.of(mapRowToAccount(rs));
            }
            
        } catch (SQLException e) {
            //Just print the exception to the console
            e.printStackTrace();

        }
        //In the case the account was not found
        return Optional.empty();
    }
    
}



