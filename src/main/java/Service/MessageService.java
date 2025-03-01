package Service;

import java.util.List;
import java.util.Optional;

import DAO.AccountDao;
import DAO.JDBCAccountDao;
import DAO.JDBCMessageDao;
import DAO.MessageDao;
import Model.Message;

public class MessageService {
    
    MessageDao messageDao;
    AccountDao accountDao;

    public MessageService(){
        this.messageDao = new JDBCMessageDao();
        this.accountDao = new JDBCAccountDao();
    }

    public MessageService(MessageDao messageDao, AccountDao accountDao){
        this.messageDao = messageDao;
        this.accountDao = accountDao;
    }

    public List<Message> getMessages(){
        return messageDao.getAllMessages();
    }

    public List<Message> getAllMessagesFromUser(int postedbyId){
        return messageDao.getAllMessagesByPostedById(postedbyId);
    }

    public Optional<Message> getMessageById(int messageId){
        return messageDao.getMessageById(messageId);
    }


    public Optional<Message> uploadNewMessage(Message newMessage){
        
        boolean messageTextIsValid =  newMessage.getMessage_text().length() > 0 && newMessage.getMessage_text().length() < 255; 
        boolean postedByUserExists = accountDao.getAccountById(newMessage.getPosted_by()).isPresent();
        
        if(!messageTextIsValid || !postedByUserExists) return Optional.empty();

        return messageDao.insertMessage(newMessage);
    }

    public Optional<Message> deleteMessageById(int messageId){


        //Call the messageDao delete message method only when we're sure the message exists
        Optional<Message> messageToDelete = messageDao.getMessageById(messageId);

        //If message does not exist, return an empty Optional
        if(messageToDelete.isEmpty()) return Optional.empty();

        
        messageDao.deleteMessageById(messageId);
        return messageToDelete;
        
    }

    public Optional<Message> updateMessageById(int idOfMessageToUpdate, String newText){
        Optional<Message> messageToUpdate = messageDao.getMessageById(idOfMessageToUpdate);
        boolean messageExists = messageToUpdate.isPresent();
        boolean messageTextIsValid =  newText.length() > 0 && newText.length() < 255; 

        //Client-side error has been made
        if(!messageTextIsValid || !messageExists) return Optional.empty();

        Message updatedMessage = new Message(idOfMessageToUpdate, messageToUpdate.get().posted_by, newText, messageToUpdate.get().getTime_posted_epoch());
        
        //If the update of the message is not successful for any reason, return an empty Optional
        return messageDao.updateMessage(updatedMessage) ? Optional.of(updatedMessage) : Optional.empty();
    
    }

    
    

}
