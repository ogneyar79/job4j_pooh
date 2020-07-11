package model.server;

import model.broker.QueueSender;
import model.broker.SubscriberStore;
import model.broker.TopicSender;
import model.connection.Conection;
import model.connection.Message;
import model.connection.MessageType;
import model.server.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerWithClient implements IServerPro, Runnable {

    private ServerSocket serverSocket;
    boolean isServerStart;

    ModelGuiServer subConections;

    private final SubscriberStore subscriberTopicStore;
    private final ConcurrentHashMap<String, String> subscriberQueue;
    private final QueueSender queueSender;
    private final TopicSender sender;

    public ServerWithClient(SubscriberStore subscriberTopicStore, ConcurrentHashMap<String, String> subscriberQueue, QueueSender queueSender, TopicSender sender) {
        this.subscriberTopicStore = subscriberTopicStore;
        this.subscriberQueue = subscriberQueue;
        this.queueSender = queueSender;
        this.sender = sender;
    }

    @Override
    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            isServerStart = true;
            System.out.println("ServerStart");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                Thread.currentThread().interrupt();
            } else {
                System.out.println(" Nothing to stop, The Server does not work");
            }
        } catch (IOException e) {
            System.out.println(" NO allow to stop server");
            e.printStackTrace();
        }
    }

    @Override
    public void acceptServer() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                Conection conection = new Conection(socket);
                String id = getIdConnection(conection);
                if (!id.equals("")) {
                    conection.send(new Message(MessageType.ID_ACCEPTED));
                    dialogSubscriber(conection, id);
                    deleteConnection(id);
                } else {
                    conection.send(new Message(MessageType.USER_INFO, "This id used or incorrect id"));
                }
            } catch (IOException e) {
                System.err.println(" No connection with server");
                e.printStackTrace();
            }
        }
    }

    public String getIdConnection(Conection conection) {
        String result = "";
        int count = 0;
        while (count != 3) {
            try {
                conection.send(new Message(MessageType.REQUEST_SUBSCRIBER_ID));
                count++;
                Message responseMessage = conection.receive();
                final String subscriberId = responseMessage.getTextMessage();  // return id?
                if (responseMessage.getTypeMessage() == MessageType.SUBSCRIBER_ID && subscriberId != null && !subConections.getSubscribersConect().containsKey(subscriberId)) {
                    subConections.addSub(subscriberId, conection);
                    return subscriberId;
                }
            } catch (Exception e) {
                System.err.println("ERROR During adding new subscriber");
            }
        }
        System.err.println(" You exceeded amount Connection We break connection");
        return result;
        // exit from method, delete connection   return String accept or not
    }


    public void dialogSubscriber(Conection conection, String subscriberId) {

        if (subscriberTopicStore.getSubscriber().containsKey(subscriberId)) {
            final Conection conectPrivate = subConections.getSubscribersConect().get(subscriberId);
            new DialogTopic(conectPrivate, sender, subscriberId).dialog();
        }

        if (subscriberQueue.containsKey(subscriberId)) {
            final Conection conectPrivate = subConections.getSubscribersConect().get(subscriberId);
            new Thread(new DialogQueue(conectPrivate, queueSender, subscriberId) {
            }).start();// hand to Queue work
        }
    }


    @Override
    public void deleteConnection(String id) {
        Conection currentConnection = subConections.getSubscribersConect().get(id);
        subConections.removeConectionSub(id);
        System.err.println(" WE interrupt connection with id \n" + "" + id + " and Socet Adress" + "" + currentConnection.getRemoteSocetAdres());
        try {
            currentConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        acceptServer();
    }
}
