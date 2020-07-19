package model.serversender.roughcopy;

import model.broker.QueueSender;
import model.connection.Connection;
import model.connection.Message;
import model.connection.MessageType;
import model.serversender.IServerPro;
import model.serversender.ModelGuiServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerPro implements IServerPro {

    private ServerSocket serverSocket;
    boolean isServerStart;

    ModelGuiServer subConections;

    private final QueueSender queueSender;

    public ServerPro(QueueSender queueSender) {
        this.queueSender = queueSender;
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
                new Thread(new ServerThread(socket)).start();
            } catch (IOException e) {
                System.err.println(" No connection with serversender");
                e.printStackTrace();
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

    private class ServerThread implements Runnable {

        private final Socket socket;

        public ServerThread(Socket socket) {
            this.socket = socket;
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
            while (!Thread.interrupted()) {
                String userId = messageId.getTextMessage();
                if (queueSender.checkSubscriberId(userId)) {
                    try {
                        connection.send(new Message(MessageType.USER_INFO, " WE CHECK Your SUBSCRIBE"));
                        connection.send(queueSender.sendResult(userId));
                        connection.send(new Message(MessageType.DISABLE_USER, " WE handed Result"));
                        deleteConnection(userId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        connection.send(new Message(MessageType.USER_INFO, "No SUBSCRIBER WITH ID"));
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
        public void run() {
            try {
                Connection connection = new Connection(socket);
                Message messageId = requestAddSubscriber(connection);
                communicateWithClient(connection, messageId);
            } catch (IOException e) {
                System.err.println(" ERROR during Communicate Server and Subscriber \n");
                e.printStackTrace();
            }
        }
    }
}


