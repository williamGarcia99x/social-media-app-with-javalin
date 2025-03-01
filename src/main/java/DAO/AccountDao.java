package DAO;

import java.util.Optional;
import Model.Account;

public interface AccountDao {

    /*
     * Creates a new account in the database. If insert is successful, it will contain a valid Account object with the newly created account id
     * If account already exists, returns an empty Optional. In the case of an error with the data source, throws a runtime exception
     */
    Optional<Account> insertAccount(Account newAccount);


    /*
     * Searches database for a user with the given credentials. Returns an empty Optional object if the user is not found. In the case of an error with the data source, throws a runtime exception
     */
    Optional<Account> verifyLoginInformation(String username, String password);

    /*
     * Searches database for a user with the given ID
     */
    Optional<Account> getAccountById(int id);

    /*
     * Searches database for a user with the username
     */
    Optional<Account> getAccountByUsername(String username);
    
}
