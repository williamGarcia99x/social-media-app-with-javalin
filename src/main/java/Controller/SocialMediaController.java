package Controller;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {


    private AccountService accountService;
    private MessageService messageService;

    public SocialMediaController(){
        accountService = new AccountService();
        messageService = new MessageService();
    }


    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("/", (ctx) -> ctx.result("main endpoint"));
        app.get("example-endpoint", this::exampleHandler);
        app.post("register", this::registerUser);
        app.post("login", this::loginUser);

        app.get("messages", this::getMessages);
        app.get("messages/{message_id}", this::getMessageById);
        app.get("accounts/{account_id}/messages", this::getAllMessagesFromUser);
        app.post("messages", this::uploadNewMessage);
        app.patch("messages/{message_id}", this::updateMessage);
        app.delete("messages/{message_id}", this::deleteMessageById);


        app.exception(JsonProcessingException.class, this::jsonProcessingExceptionHandler);
        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("HELLOOOOO WOOOOOOOOOOORRRRRLLLLLLLLD");
    }

    
    private void registerUser(Context ctx) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        //In the case of a JsonProcessingException, throw it out and the appropriate controller handler will catch it
        Account newAccount = mapper.readValue(ctx.body(), Account.class);
        try {
            Optional<Account> createdAccount = accountService.createAccount(newAccount);
            //If the optional is present, then the request was handled successfully. Otherwise, a client made a faulty request. 
            if(createdAccount.isPresent()){
                ctx.json(createdAccount.get());
            }
            else {
                ctx.status(400);
            }
            
        } catch (RuntimeException e) {
            //An exception will only be thrown in the case that there is an issue in any of the underlying units not due to user error. 
            ctx.status(500);
        }
    }

    private void loginUser(Context ctx) throws JsonProcessingException{
        
        ObjectMapper mapper = new ObjectMapper();
        //In the case of a JsonProcessingException, throw it out and the appropriate controller handler will catch it
        Account accountToAuthenticate = mapper.readValue(ctx.body(), Account.class);
        try{
            Optional<Account> foundAccount = accountService.authenticateUser(accountToAuthenticate);
            if(foundAccount.isPresent()){
                ctx.json(foundAccount.get());
            }
            else{
                ctx.status(401);
            }
        } catch(RuntimeException e){
            //An exception will only be thrown in the case that there is an issue in any of the underlying units not due to user error. 
            ctx.status(500);
        }
        
    }

    //Messages Controller portion
    private void getMessages(Context ctx){
        //Relatively straight forward logic. Only in the case of an server internal error do we return a status code of 500. 
        try {
            ctx.json(messageService.getMessages());
        } catch (RuntimeException e) {
            ctx.status(500);
        }
    }

    private void getMessageById(Context ctx){
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Optional<Message> messageFound = messageService.getMessageById(messageId);
            ctx.json(messageFound.isPresent() ? messageFound.get() : "");
        } catch (RuntimeException e) {
            //user did not provide the right data type format for the message_id
            if(e instanceof NumberFormatException){
                ctx.status(400);
            }
            //else server error occurred
            ctx.status(500);

            
        }

    }

    private void uploadNewMessage(Context ctx) throws JsonProcessingException{

        ObjectMapper mapper = new ObjectMapper();
        //In the case of a JsonProcessingException, throw it out and the appropriate controller handler will catch it
        Message message = mapper.readValue(ctx.body(), Message.class);
        try{
            Optional<Message> createdMessage = messageService.uploadNewMessage(message);
            if(createdMessage.isPresent()){
                ctx.json(createdMessage.get());
            }
            else {
                //Message did not pass validation due to client error.
                ctx.status(400);
            }
        }catch(RuntimeException e){
            //An exception will only be thrown in the case that there is an issue in any of the underlying units not due to user error. 
            ctx.status(500);

        }
        
    }

    private void deleteMessageById(Context ctx){

        try{
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Optional<Message> messageDeleted = messageService.deleteMessageById(messageId);
            //if the message was deleted, return the JSON representation of the message. Otherwise, return an empty string
            ctx.json(messageDeleted.isPresent() ? messageDeleted.get() : "");
        }catch(RuntimeException e){
            //server error occurred
            ctx.status(500);
        }

    }

    private void updateMessage(Context ctx) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        //In the case of a JsonProcessingException, throw it out and the appropriate controller handler will catch it
        
        Message message = mapper.readValue(ctx.body(), Message.class);
        try{
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Optional<Message> messageUpdated = messageService.updateMessageById(messageId, message.getMessage_text());
            //if the message was deleted, return the JSON representation of the message. Otherwise, return an empty string
            if(messageUpdated.isPresent()){
                ctx.json(messageUpdated.get());
            }
            else ctx.status(400);
        }catch(RuntimeException e){
            //else server error occurred
            ctx.status(500);
        }

    }

   

    public void getAllMessagesFromUser(Context ctx){
        try{
            int accountId = Integer.parseInt(ctx.pathParam("account_id"));
            ctx.json(messageService.getAllMessagesFromUser(accountId));   
        }catch(RuntimeException e){
            //Server error occured
            ctx.status(500);
        }
    }


    //Assume any JSON parsing errors are due to an object with the incorrect structure
    private void jsonProcessingExceptionHandler(JsonProcessingException e, Context ctx){
        ctx.status(400);
        ctx.result("Bad input");
    }


}