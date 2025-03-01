package Controller;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import DAO.JDBCAccountDao;
import Model.Account;
import Service.AccountService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {


    private AccountService accountService;

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
        app.get("/", (ctx) -> ctx.result("main endpoint"));
        app.get("example-endpoint", this::exampleHandler);
        app.post("register", this::registerUser);
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




    


}