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

    
    
      //If no row was deleted, it's likely that it was already deleted or the row never existed. In either case
      //no error needs to be thrown. Just return the boolean
    @Override
    public boolean deleteMessageById(int messageId) {

        Connection connection = ConnectionUtil.getConnection();
        String deleteSQL = "DELETE FROM message WHERE message_id = ?;";
        try{
            PreparedStatement ps = connection.prepareStatement(deleteSQL);
            ps.setInt(1, messageId);
            int rowsAffected = ps.executeUpdate();
            if(rowsAffected == 1) return true;
        } catch(SQLException e){
            //Only in the case of a database access error
            throw new RuntimeException(e);
        }
        return false;
    }


   
    @Override
    public boolean updateMessage(Message updatedMessage) {
        Connection connection = ConnectionUtil.getConnection();
        String updateSQL = "UPDATE message SET message_text = ? WHERE message_id = ?;";
        try {
            PreparedStatement ps = connection.prepareStatement(updateSQL);
            ps.setString(1, updatedMessage.getMessage_text());
            ps.setInt(2, updatedMessage.getMessage_id());
            int rowsAffected = ps.executeUpdate();
            if(rowsAffected == 1) return true;
        } catch (SQLException e) {
            // In the case of a database access error
            throw new RuntimeException(e);
        }

        //The only case in which an UPDATE into the message table may be unsuccessful is when 
        //there is a database access error. In such a case, the Runtime exception above is thrown. 
        return false;

    }

    @Override
    public List<Message> getAllMessagesByPostedById(int postedbyId) {
        List<Message> messages = new ArrayList<>();
        Connection connection = ConnectionUtil.getConnection();
        String selectSQL = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message WHERE posted_by = ?;";
        try {
            PreparedStatement ps = connection.prepareStatement(selectSQL);
            ps.setInt(1, postedbyId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                messages.add(mapRowToMessage(rs));
            }
            return messages;
        } catch (SQLException e) {
            //In the case of a database access error
            throw new RuntimeException(e);
        }

    }
    

    @Override
    public Optional<Message> insertMessage(Message message) {
        Connection connection = ConnectionUtil.getConnection();
        String insertSQL = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?,?,?);";
        try {
            PreparedStatement ps = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());
            ps.executeUpdate();
            ResultSet pkeyResultSet = ps.getGeneratedKeys();
            if(pkeyResultSet.next()){
                int pkey = pkeyResultSet.getInt(1);
                return Optional.of(new Message(pkey, message.posted_by,message.getMessage_text(), message.getTime_posted_epoch()));
            }
        } catch (SQLException e) {
            // In the case of a database access error
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        //The only case in which an insert into the message table may be unsuccessful is when 
        //there is a database access error. In such a case, the Runtime exception above is thrown. 
        return Optional.empty();

    }


    public Message mapRowToMessage(ResultSet row) throws SQLException{
        int id = row.getInt("message_id");
        int postedBy = row.getInt("posted_by");
        String messageText = row.getString("message_text");
        long timePostedEpoch = row.getLong("time_posted_epoch");
        return new Message(id,postedBy, messageText, timePostedEpoch);

    }

   
    
}
