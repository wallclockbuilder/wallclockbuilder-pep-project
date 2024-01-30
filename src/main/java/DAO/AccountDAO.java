package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.Account;
import Model.Message;
import Util.ConnectionUtil;

public class AccountDAO {

    public Account insertAccount(Account account) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            //Write SQL logic here. When inserting, you only need to define the departure_city and arrival_city
            //values (two columns total!)
            String sql = "INSERT INTO account(username, password) VALUES(?, ?)" ;
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            //write preparedStatement's setString and setInt methods here.
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());

            preparedStatement.executeUpdate();
            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if(pkeyResultSet.next()){
                int generated_flight_id = (int) pkeyResultSet.getLong(1);
                return new Account(generated_flight_id, account.getUsername(), account.getPassword());
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Account verifyLogin(Account account) throws SQLException {
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT * FROM account WHERE username=? AND password=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                int account_id = rs.getInt("account_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                Account verifiedAccount = new Account(account_id, username, password);
                return verifiedAccount;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        return null;
    }

    public Account findAccountByPosterId(int poster_id)throws SQLException{
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT * FROM account WHERE account_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, poster_id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Account account = new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
                return account;
            }
        } catch (Exception e) {
            // handle exception
            System.out.println(e.getMessage());

        }
        
        return null;
    }
    

    public Message insertMessage(Message message) throws SQLException {
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "INSERT INTO message(posted_by, message_text, time_posted_epoch) VALUES(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());
            preparedStatement.executeUpdate();
            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if(pkeyResultSet.next()){
                int generated_account_id =  pkeyResultSet.getInt(1);
                return new Message(generated_account_id, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
            }
        } catch (Exception e) {
            // handle exception
            System.out.println(e.getMessage());

        }
        
        return null;
    }

    public List<Message> getAllMessages() {
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            String sql = "SELECT * FROM message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Message foundMessage = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );
                messages.add(foundMessage);
            }
        } catch (Exception e) {
            // handle exception
            System.out.println(e.getMessage());
        }
        return messages;
    }

    public Message getMessageById(int message_id) {
        Connection connection = ConnectionUtil.getConnection();
        Message message = null;
        try {
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                    );
            }
        } catch (Exception e) {
            // handle exception
            System.out.println(e.getMessage());

        }
        return message;
    }

    public Message deleteMessageByMessageId(int message_id) {
        //fetch message 
        //then delete message
        Message message = null;
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql1 = "SELECT * FROM message WHERE message.message_id = ?";
            PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
            preparedStatement1.setInt(1, message_id);
            ResultSet rs = preparedStatement1.executeQuery();
            while(rs.next()){
                message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );
            }

            String sql2 = "DELETE FROM message WHERE message.message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setInt(1, message_id);
            int row_count = preparedStatement.executeUpdate();


            if(row_count == 0){
                message = null;
            }
            
        } catch (Exception e) {
            // handle exception
            System.out.println(e.getMessage());

        }
        return message;
    }

    public Message updateMessageById(int message_id, Message new_message) {
       
        Message message = null;
        Connection connection = ConnectionUtil.getConnection();
        try {
             //update message
             //then fetch message
            String sql1 = "UPDATE message SET message_text = ? WHERE message.message_id = ?";
            PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
            preparedStatement1.setString(1, new_message.getMessage_text());
            preparedStatement1.setInt(2, message_id);
            int row_count = preparedStatement1.executeUpdate();

            String sql2 = "SELECT * FROM message WHERE message.message_id = ?";
            PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
            preparedStatement2.setInt(1, message_id);
            ResultSet rs = preparedStatement2.executeQuery();
            while(rs.next()){
                message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );
            }
            
            if(row_count == 0){
                message = null;
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
        return message;
    }
}
