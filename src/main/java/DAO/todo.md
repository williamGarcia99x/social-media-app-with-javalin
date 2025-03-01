You will need to design and create your own DAO classes from scratch. 
You should refer to prior mini-project lab examples and course material for guidance.

Please refrain from using a 'try-with-resources' block when connecting to your database. 
The ConnectionUtil provided uses a singleton, and using a try-with-resources will cause issues in the tests.



As a user, I should be able to create a new Account on the endpoint POST localhost:8080/register. The body will contain a representation of a JSON Account, but will not contain an account_id.

The registration will be successful if and only if the username is not blank, the password is at least 4 characters long, and an Account with that username does not already exist. If all these conditions are met, the response body should contain a JSON of the Account, including its account_id. The response status should be 200 OK, which is the default. The new account should be persisted to the database.

If the registration is not successful, the response status should be 400. (Client error)


//This is the DAO method for creating a new user in the users table. It will not perform any business logic since it will be done in the Service class. 
public Account insertUser(Account newUser){

    String insertSQL = ""

}

