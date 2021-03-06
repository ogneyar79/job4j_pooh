package model.serversender.roughcopy;

import model.broker.TopicSender;
import model.connection.Connection;
import model.connection.Message;
import model.connection.MessageType;
import model.serversender.IServerPro;
import model.serversender.ModelGuiServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TopicServer implements IServerPro, Runnable {

    private ServerSocket serverSocket;
    boolean isServerStart;

    private final int port;

    ModelGuiServer subConections;

    private final TopicSender topicSender;
    //  private final SubscriberStore subscriberStore;

    public TopicServer(ServerSocket serverSocket, TopicSender topicSender, int port) {
        this.serverSocket = serverSocket;
        this.topicSender = topicSender;
        this.port = port;
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
            System.out.println(" NO allow to stop serversender");
            e.printStackTrace();
        }

    }

    @Override
    public void acceptServer() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                Connection connection = new Connection(socket);
                requestAddSubscriber(connection);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Message requestAddSubscriber(Connection connection) {

        int count = 0;
        while (true) {
            if (count > 3) {
                System.err.println(" You exceeded amount Connection We break connection");
                return new Message(MessageType.DISABLE_USER, " You exceeded amount Connection We break connection");
            }
            try {
                connection.send(new Message(MessageType.REQUEST_SUBSCRIBER_ID));
                Message responseMessage = connection.receive();
                String subscriberId = responseMessage.getTextMessage();
                if (responseMessage.getTypeMessage() == MessageType.SUBSCRIBER_ID && subscriberId != null && !subConections.getConnectionMap().containsKey(subscriberId)) {
                    subConections.addSub(subscriberId, connection);
                    connection.send(new Message(MessageType.ID_ACCEPTED));
                    return new Message(MessageType.ID_USED, subscriberId);
                } else {
                    connection.send(new Message(MessageType.ID_USED));
                    count++;
                }
            } catch (Exception e) {
                System.err.println("ERROR During adding new subscriber");
            }
        }
    }

    public void communicateWithClient(Connection connection, Message messageId) {
        while (Thread.interrupted()) {
            String userId = messageId.getTextMessage();
            if (topicSender.checkSubscriberId(userId)) {
                try {
                    connection.send(new Message(MessageType.USER_INFO, " WE CHECK Your SUBSCRIBE"));

                    do {
                        connection.send(topicSender.sendResult(userId));
                        connection.send(new Message(MessageType.USER_INFO, " WE handed Result"));
                    }
                    while (topicSender.checkMessage(userId));
                    deleteConnection(userId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    connection.send(new Message(MessageType.USER_INFO, "No Your ID"));
                    connection.send(new Message(MessageType.REFUSING, "REFUSE"));
                    deleteConnection(userId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Message message = connection.receive();
                if (message.getTypeMessage() == MessageType.DISABLE_USER) {
                    deleteConnection(userId);
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void deleteConnection(String id) {
        Connection currentConnection = subConections.getConnectionMap().get(id);
        subConections.removeConectionSub(id);
        Thread.currentThread().interrupt();
        System.err.println(" WE interrupt connection with id \n" + "" + id + " and Socet Adress" + "" + currentConnection.getRemoteSocetAdres());
        try {
            currentConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        startServer(this.port);
        acceptServer();
    }
}
