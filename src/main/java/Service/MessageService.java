package Service;

import java.util.List;
import java.util.Optional;

import DAO.JDBCMessageDao;
import DAO.MessageDao;
import Model.Message;

public class MessageService {
    
    MessageDao messageDao;

    public MessageService(){
        this.messageDao = new JDBCMessageDao();
    }

    public MessageService(MessageDao messageDao){
        this.messageDao = messageDao;
    }

    public List<Message> getMessages(){
        return messageDao.getAllMessages();
    }

    public Optional<Message> getMessageById(int messageId){
        return messageDao.getMessageById(messageId);

    }

}
