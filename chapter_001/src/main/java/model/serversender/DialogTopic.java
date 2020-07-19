package model.serversender;

import model.broker.TopicSender;
import model.connection.Connection;
import model.connection.Message;
import model.connection.MessageType;

import java.io.IOException;

public class DialogTopic {

    public void dialog(Connection connection, TopicSender sender, String userId) {
        try {
            connection.send(new Message(MessageType.USER_INFO, " WE CHECK Your SUBSCRIBE"));
            while (sender.checkMessage(userId)) {
                connection.send(sender.sendResult(userId));
            }
            connection.send(new Message(MessageType.DISABLE_USER, " WE handed Result"));
            System.err.println(" WE interrupt connection with id \n" + "" + userId + " and Socet Adress" + "" + connection.getRemoteSocetAdres());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
