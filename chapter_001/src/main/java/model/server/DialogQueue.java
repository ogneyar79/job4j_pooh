package model.server;

import model.broker.QueueSender;
import model.connection.Conection;
import model.connection.Message;
import model.connection.MessageType;

import java.io.IOException;

public class DialogQueue implements Runnable {

    private final Conection conection;
    private final QueueSender queueSender;
    private final String userId;

    public DialogQueue(Conection conection, QueueSender queueSender, String userId) {
        this.conection = conection;

        this.queueSender = queueSender;
        this.userId = userId;
    }

    public void dialog() {
        try {
            conection.send(new Message(MessageType.USER_INFO, " WE CHECK Your SUBSCRIBE"));
            conection.send(queueSender.sendResult(userId));
            conection.send(new Message(MessageType.DISABLE_USER, " WE handed Result"));
            System.err.println(" WE interrupt connection with id \n" + "" + userId + " and Socet Adress" + "" + conection.getRemoteSocetAdres());
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
