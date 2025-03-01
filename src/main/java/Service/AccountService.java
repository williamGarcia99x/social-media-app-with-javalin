package Service;

import java.util.Optional;

import DAO.AccountDao;
import DAO.JDBCAccountDao;
import Model.Account;

public class AccountService {
    
    private AccountDao accountDao;

    /*No-args constructor for an AccountService object. Creates an instance of JDBCAccountDao */
    public AccountService(){
        this.accountDao = new JDBCAccountDao();
    }

    /*Constructor for AccountService when an AccountDao instance is provided. Allows for dependency injection */
    public AccountService(AccountDao accountDao){
        this.accountDao = accountDao;
    }

    //Returns an empty Optional only if there was a client error like choosing a username that already exists. An exeption may be thrown from the underlying DAO layer if there is a 
    //an issue with accessing the datasource
    public Optional<Account> createAccount(Account newAccount){
       
         //Validate newAccount information before calling the insert DAO method
        boolean isUsernameValid = newAccount.getUsername().length() > 0;
        boolean isPasswordValid = newAccount.getPassword().length() >= 4;
       
        boolean doesAccountAlreadyExists = accountDao.getAccountByUsername(newAccount.getUsername()).isPresent();
        if(!isUsernameValid || !isPasswordValid || doesAccountAlreadyExists){
            return Optional.empty();
        }
        
        //At this point, it's safe to create the insertion
        return accountDao.insertAccount(newAccount);
        
    }



}
