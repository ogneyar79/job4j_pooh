package model.serversender;

import model.broker.QueueSender;
import model.connection.Connection;
import model.connection.Message;
import model.connection.MessageType;

import java.io.IOException;

public class DialogQueue implements Runnable {

    private final Connection connection;
    private final QueueSender queueSender;
    private final String userId;

    public DialogQueue(Connection connection, QueueSender queueSender, String userId) {
        this.connection = connection;

        this.queueSender = queueSender;
        this.userId = userId;
    }

    public void dialog() {
        try {
            connection.send(new Message(MessageType.USER_INFO, " WE CHECK Your SUBSCRIBE"));
            connection.send(queueSender.sendResult(userId));
            connection.send(new Message(MessageType.DISABLE_USER, " WE handed Result"));
            System.err.println(" WE interrupt connection with id \n" + "" + userId + " and Socet Adress" + "" + connection.getRemoteSocetAdres());
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            dialog();
        }
    }
}
