package Service;
import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    AccountDAO accountDAO;
    /**
     * No-args constructor for a flightService instantiates a plain flightDAO.
     * There is no need to modify this constructor.
     */
    public AccountService(){
        accountDAO = new AccountDAO();
    }

    /**
     * Constructor for a accountService when a accountDAO is provided.
     * This is used for when a mock accountDAO that exhibits mock behavior is used in the test cases.
     * This would allow the testing of AccountService independently of AccountDAO.
     * @param flightDAO
     */
    public AccountService(AccountDAO accountDAO){
        this.accountDAO = accountDAO;
    }

    // @param account an object representing a new Account.
    //  @return the newly added account if the add operation was successful, including the account_id.
    // - The registration will be successful if and only if the username is not blank, 
    // the password is at least 4 characters long, and an Account with that username does not already exist. 
    public Account addAccount(Account account){
        if(account.getUsername() != "" & account.getPassword().length() >= 4){
            return this.accountDAO.insertAccount(account);
        }else{
            return null;
        }
    }

}
