package DAO;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Model.Message;
import Util.ConnectionUtil;

public class JDBCMessageDao implements MessageDao {

    @Override
    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        Connection connection = ConnectionUtil.getConnection();
        String selectSQL = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message;";

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectSQL);
            while(rs.next()) {
                messages.add(mapRowToMessage(rs));
            }
            return messages;
            
        } catch (SQLException e) {
            //In the case of database access error
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    
    }

    @Override
    public Optional<Message> getMessageById(int id) {
        Connection connection = ConnectionUtil.getConnection();
        String selectSQL = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message WHERE message_id = ?;";
        try {
            PreparedStatement ps = connection.prepareStatement(selectSQL);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return Optional.of(mapRowToMessage(rs));
            }
            
        } catch (SQLException e) {
            //Just print the exception to the console
            e.printStackTrace();
            //In the case of database access error
            throw new RuntimeException(e);
        }
        //In the case the message was not found
        return Optional.empty();
    }
    

    @Override
    public boolean deleteMessageById(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteMessageById'");
    }

    @Override
    public boolean updateMessageById(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateMessageById'");
    }

    @Override
    public List<Message> getAllMessagesFromUser(int accountId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllMessagesFromUser'");
    }


    public Message mapRowToMessage(ResultSet row) throws SQLException{
        int id = row.getInt("message_id");
        int postedBy = row.getInt("posted_by");
        String messageText = row.getString("message_text");
        long timePostedEpoch = row.getLong("time_posted_epoch");
        return new Message(id,postedBy, messageText, timePostedEpoch);

    }
    
}
