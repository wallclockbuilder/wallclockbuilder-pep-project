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
 * Endpoints and handlers for controller.
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
        app.post("register", this::postAccountHandler);
        app.post("login", this::postLoginHandler);
        app.post("messages", this::postMessagesHandler);
        app.get("messages", this::getMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByMessageId);
        app.delete("/messages/{message_id}", this::deleteMessageByMessageId);
        app.patch("/messages/{message_id}", this::updateMessageByMessageId);
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountId);
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

    // ## 4: Our API should be able to retrieve all messages.

    // As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages.

    // - The response body should contain a JSON representation of a list containing all messages retrieved 
    // from the database. It is expected for the list to simply be empty if there are no messages. 
    // The response status should always be 200, which is the default.
    private void getMessagesHandler(Context ctx){
        ctx.json(accountService.getAllMessages());
    }

    //     ## 5: Our API should be able to retrieve a message by its ID.

    // As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages/{message_id}.
    // - The response body should contain a JSON representation of the message identified by the message_id. 
    // It is expected for the response body to simply be empty if there is no such message. The response status 
    // should always be 200, which is the default.
    private void getMessageByMessageId(Context ctx){
        int message_id = Integer.valueOf(ctx.pathParam("message_id"));
        Message message = accountService.getMessageByMessageId(message_id);
        if( message == null){
            ctx.status(200);
        } else{
            ctx.json(message);
        }
    }

//     ## 6: Our API should be able to delete a message identified by a message ID.

// As a User, I should be able to submit a DELETE request on the endpoint DELETE localhost:8080/messages/{message_id}.

// - The deletion of an existing message should remove an existing message from the database. If the message existed, 
// the response body should contain the now-deleted message. The response status should be 200, which is the default.
// - If the message did not exist, the response status should be 200, but the response body should be empty. This is 
// because the DELETE verb is intended to be idempotent, ie, multiple calls to the DELETE endpoint should respond 
// with the same type of response.
private void deleteMessageByMessageId(Context ctx){
    int message_id = Integer.valueOf(ctx.pathParam("message_id"));
    Message message = accountService.deleteMessageByMessageId(message_id);
    if(message == null){
        ctx.status(200);
    }else{
        ctx.json(message);
    }
}

// ## 7: Our API should be able to update a message text identified by a message ID.

// As a user, I should be able to submit a PATCH request on the endpoint PATCH localhost:8080/messages/{message_id}.
//  The request body should contain a new message_text values to replace the message identified by message_id. 
// The request body can not be guaranteed to contain any other information.

// - The update of a message should be successful if and only if the message id already exists and the new message_text
//  is not blank and is not over 255 characters. If the update is successful, the response body should contain the 
// full updated message (including message_id, posted_by, message_text, and time_posted_epoch), and the response status
//  should be 200, which is the default. The message existing on the database should have the updated message_text.
// - If the update of the message is not successful for any reason, the response status should be 400. (Client error)
    private void updateMessageByMessageId(Context ctx) throws JsonMappingException, JsonProcessingException{
        ObjectMapper om = new ObjectMapper();
        int message_id = Integer.valueOf(ctx.pathParam("message_id"));
        Message new_message = om.readValue(ctx.body(), Message.class);
        Message updated_message = accountService.updateMessageByMessageId(message_id, new_message);
        if(new_message.getMessage_text() == "" || new_message.getMessage_text().length() > 255 || updated_message == null){
            ctx.status(400);
        }else{
            ctx.json(updated_message);
        }
    }

//     # 8: Our API should be able to retrieve all messages written by a particular user.

// As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/accounts/{account_id}/messages.
// - The response body should contain a JSON representation of a list containing all messages posted by a particular user,
//  which is retrieved from the database. It is expected for the list to simply be empty if there are no messages. 
// The response status should always be 200, which is the default.
    private void getMessagesByAccountId(Context ctx){
        int account_id =Integer.valueOf(ctx.pathParam("account_id"));
        ctx.json(accountService.getMessagesByAccountId(account_id));
    }
}