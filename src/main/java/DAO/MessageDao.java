package DAO;

import java.util.List;
import java.util.Optional;

import Model.Message;

public interface MessageDao {
    
    /*Returns a list of all the messages. Throws an exception in the case of a data access error*/
    List<Message> getAllMessages();

    Optional<Message> getMessageById(int id);

    boolean deleteMessageById(int id);

    boolean updateMessage(Message updatedMessage);

    List<Message> getAllMessagesByPostedById(int postedbyId);

    Optional<Message> insertMessage(Message message);
}
