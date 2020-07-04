package model.server;

import model.broker.SubscriberStore;
import model.broker.TopicSender;
import model.connection.Conection;
import model.connection.Message;
import model.connection.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TopicServer implements IServerPro {


    private ServerSocket serverSocket;
    boolean isServerStart;

    ModelGuiServer subConections;

    private final TopicSender topicSender;
    //  private final SubscriberStore subscriberStore;

    public TopicServer(ServerSocket serverSocket, TopicSender topicSender, SubscriberStore subscriberStore) {
        this.serverSocket = serverSocket;
        this.topicSender = topicSender;
        //      this.subscriberStore = subscriberStore;
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
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                Conection conection = new Conection(socket);
                requestAddSubscriber(conection);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Message requestAddSubscriber(Conection conection) {

        int count = 0;
        while (true) {
            if (count > 3) {
                System.err.println(" You exceeded amount Connection We break connection");
                return new Message(MessageType.DISABLE_USER, " You exceeded amount Connection We break connection");
            }
            try {
                conection.send(new Message(MessageType.REQUEST_SUBSCRIBER_ID));
                Message responseMessage = conection.receive();
                String subscriberId = responseMessage.getTextMessage();
                if (responseMessage.getTypeMessage() == MessageType.SUBSCRIBER_ID && subscriberId != null && !subscriberId.isBlank() && !subConections.getSubscribersConect().containsKey(subscriberId)) {
                    subConections.addSub(subscriberId, conection);
                    conection.send(new Message(MessageType.ID_ACCEPTED));
                    return new Message(MessageType.ID_USED, subscriberId);
                } else {
                    conection.send(new Message(MessageType.ID_USED));
                    count++;

                }
            } catch (Exception e) {
                System.err.println("ERROR During adding new subscriber");
            }
        }
    }

    public void communicateWithClient(Conection conection, Message messageId) {
        while (Thread.interrupted()) {
            String userId = messageId.getTextMessage();
            if (topicSender.checkSubscriberId(userId)) {
                try {
                    conection.send(new Message(MessageType.USER_INFO, " WE CHECK Your SUBSCRIBE"));

                    do {
                        conection.send(topicSender.sendResult(userId));
                        conection.send(new Message(MessageType.DISABLE_USER, " WE handed Result"));
                    }
                    while (topicSender.checkMessage(userId));
                    deleteConnection(userId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    conection.send(new Message(MessageType.USER_INFO, "No SUBSCRIBER WITH ID"));
                    conection.send(new Message(MessageType.REFUSING, "REFUSE"));
                    deleteConnection(userId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Message message = conection.receive();
                if (message.getTypeMessage() == MessageType.DISABLE_USER) {
                    deleteConnection(userId);
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void deleteConnection(String id) {
        Conection currentConnection = subConections.getSubscribersConect().get(id);
        subConections.removeConectionSub(id);
        Thread.currentThread().interrupt();
        System.err.println(" WE interrupt connection with id \n" + "" + id + " and Socet Adress" + "" + currentConnection.getRemoteSocetAdres());
        try {
            currentConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
