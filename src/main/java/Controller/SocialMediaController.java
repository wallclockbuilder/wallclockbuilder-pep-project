package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Service.AccountService;
import Model.Message;
/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    AccountService accountService;
    public SocialMediaController(){
        accountService = new AccountService();
    }
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("example-endpoint", this::exampleHandler);
        // ## 1: Our API should be able to process new User registrations.
        app.post("register", this::postAccountHandler);
        app.post("login", this::postLoginHandler);
        app.post("messages", this::postMessagesHandler);
                    
        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }


        // As a user, I should be able to create a new Account on the endpoint POST localhost:8080/register. 
        // The body will contain a representation of a JSON Account, but will not contain an account_id.
        
        // - The registration will be successful if and only if the username is not blank, 
        // the password is at least 4 characters long, and an Account with that username does not already exist. 
        // If all these conditions are met, the response body should contain a JSON of the Account, including its account_id. 
        // The response status should be 200 OK, which is the default. The new account should be persisted to the database.
        // - If the registration is not successful, the response status should be 400. (Client error)
    private void postAccountHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Account account = om.readValue(ctx.body(), Account.class);
        Account addedAccount = accountService.addAccount(account);
        if(addedAccount == null){
            ctx.status(400);
        } else{
            ctx.json(om.writeValueAsString(addedAccount));
        }
    }
        //## 2: Our API should be able to process User logins.

                    // As a user, I should be able to verify my login on the endpoint POST localhost:8080/login.

            //  The request body will contain a JSON representation of an Account, not containing an account_id.

            // - The login will be successful if and only if the username and password provided in the request body
            //  JSON match a real account existing on the database. If successful, the response body should contain a
            //  JSON of the account in the response body, including its account_id. The response status should be 200 OK,
            //  which is the default.
            // - If the login is not successful, the response status should be 401. (Unauthorized)  
    private void postLoginHandler(Context ctx) throws JsonProcessingException, SQLException{
        ObjectMapper om = new ObjectMapper();
        Account account = om.readValue(ctx.body(), Account.class);
        Account verifiedAccount = accountService.verifyLogin(account);
        if(verifiedAccount == null){
            ctx.status(401);
        }else{
            ctx.json(om.writeValueAsString(verifiedAccount));
        }
    }


    //         ## 3: Our API should be able to process the creation of new messages.

    // As a user, I should be able to submit a new post on the endpoint POST localhost:8080/messages. 
    // The request body will contain a JSON representation of a message, which should be persisted 
    // to the database, but will not contain a message_id.

    // If successful, 
    // the response body should contain a JSON of the message, including its message_id. The response 
    // status should be 200, which is the default. The new message should be persisted to the database.
    // - If the creation of the message is not successful, the response status should be 400. 
    // (Client error)
    private void postMessagesHandler(Context ctx) throws JsonMappingException, JsonProcessingException, SQLException{
        ObjectMapper om = new ObjectMapper();
        Message message = om.readValue(ctx.body(), Message.class);
        Message addedMessage = accountService.createMessage(message);
        if(addedMessage == null){
            ctx.status(400);
        }else{
            ctx.json(om.writeValueAsString(addedMessage));
        }
    }

}