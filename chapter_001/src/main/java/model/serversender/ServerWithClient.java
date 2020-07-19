package model.serversender;

import model.broker.QueueSender;
import model.broker.TopicSender;
import model.connection.Connection;
import model.connection.Message;
import model.connection.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerWithClient implements IServerPro, Runnable {

    private ServerSocket serverSocket;
    boolean isServerStart;

    private final ModelGuiServer subConections;


    private final QueueSender queueSender;
    private final TopicSender topicSender;

    public ServerWithClient(ModelGuiServer subConections, QueueSender queueSender, TopicSender topicSender) {
        this.subConections = subConections;
        this.queueSender = queueSender;
        this.topicSender = topicSender;
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
            System.out.println(" NO allow to stop serversender");
            e.printStackTrace();
        }
    }

    @Override
    public void acceptServer() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                Connection connection = new Connection(socket);
                String id = getIdConnection(connection);
                if (!id.equals("")) {
                    connection.send(new Message(MessageType.ID_ACCEPTED));
                    dialogSubscriber(id);
                    deleteConnection(id);
                } else {
                    connection.send(new Message(MessageType.USER_INFO, "This id used or incorrect id"));
                }
            } catch (IOException e) {
                System.err.println(" No connection with serversender");
                e.printStackTrace();
            }
        }
    }

    public String getIdConnection(Connection connection) {
        String result = "";
        int count = 0;
        while (count != 3) {
            try {
                connection.send(new Message(MessageType.REQUEST_SUBSCRIBER_ID));
                count++;
                Message responseMessage = connection.receive();
                final String subscriberId = responseMessage.getTextMessage();  // return id?
                if (responseMessage.getTypeMessage() == MessageType.SUBSCRIBER_ID && subscriberId != null && !subConections.getConnectionMap().containsKey(subscriberId)) {
                    subConections.addSub(subscriberId, connection);
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


    public void dialogSubscriber(String subscriberId) {

        if (topicSender.checkSubscriberId(subscriberId)) {
            final Connection conectPrivate = subConections.getConnectionMap().get(subscriberId);
            new DialogTopic().dialog(conectPrivate, this.topicSender, subscriberId);
        }

        if (queueSender.checkSubscriberId(subscriberId)) {
            final Connection conectPrivate = subConections.getConnectionMap().get(subscriberId);
            new Thread(new DialogQueue(conectPrivate, this.queueSender, subscriberId) {
            }).start();// hand to Queue work
        }
    }

    @Override
    public void deleteConnection(String id) {
        Connection currentConnection = subConections.getConnectionMap().get(id);
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
