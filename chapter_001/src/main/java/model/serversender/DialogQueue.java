package model.serversender;

import model.broker.QueueSender;
import model.connection.Connection;
import model.connection.Message;
import model.connection.MessageType;

import java.io.IOException;

public class DialogQueue {

    public void dialog(Connection connection, QueueSender queueSender, String userId) {
        try {
            connection.send(new Message(MessageType.USER_INFO, " WE CHECK Your SUBSCRIBE"));
            connection.send(queueSender.sendResult(userId));
            connection.send(new Message(MessageType.DISABLE_USER, " WE handed Result"));
            System.err.println(" WE interrupt connection with id \n" + "" + userId + " and Socet Adress" + "" + connection.getRemoteSocetAdres());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
