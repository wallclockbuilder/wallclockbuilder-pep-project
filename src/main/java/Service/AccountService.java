package Service;
import java.sql.SQLException;
import java.util.List;

import DAO.AccountDAO;
import Model.Account;
import Model.Message;

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

    public Account verifyLogin(Account account) throws SQLException {
        Account verifiedAccount = this.accountDAO.verifyLogin(account);
        return verifiedAccount;
    }
    
    // - The creation of the message will be successful if and only if the message_text is not blank, 
    // is not over 255 characters, and posted_by refers to a real, existing user.
    public Message createMessage(Message message) throws SQLException{
        int posted_by = message.getPosted_by();
        Account posters_account = this.accountDAO.findAccountByPosterId(posted_by);

        if(message.getMessage_text() == "" || message.getMessage_text().length() > 255 || posters_account == null){
            return null;
        } else {
            Message createdMessage = this.accountDAO.insertMessage(message);
            return createdMessage;
        }
    }

    public List<Message> getAllMessages() {
        return this.accountDAO.getAllMessages();
    }

}
