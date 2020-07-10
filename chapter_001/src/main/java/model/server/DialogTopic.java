package model.server;

import model.broker.QueueSender;
import model.broker.TopicSender;
import model.connection.Conection;
import model.connection.Message;
import model.connection.MessageType;

import java.io.IOException;

public class DialogTopic {

    private final Conection conection;
    private final TopicSender sender;
    private final String userId;


    public DialogTopic(Conection conection, TopicSender sender, String userId) {
        this.conection = conection;
        this.sender = sender;
        this.userId = userId;
    }

    public void dialog() {
        try {
            conection.send(new Message(MessageType.USER_INFO, " WE CHECK Your SUBSCRIBE"));
            while (sender.checkMessage(userId)) {
                conection.send(sender.sendResult(userId));
            }
            conection.send(new Message(MessageType.DISABLE_USER, " WE handed Result"));

            System.err.println(" WE interrupt connection with id \n" + "" + userId + " and Socet Adress" + "" + conection.getRemoteSocetAdres());
            conection.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
