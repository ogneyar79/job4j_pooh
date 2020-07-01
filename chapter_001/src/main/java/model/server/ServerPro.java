package model.server;

import model.broker.QueueSender;
import model.connection.Conection;
import model.connection.Message;
import model.connection.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerPro {

    private ServerSocket serverSocket;
    boolean isServerStart;

    ModelGuiServer subConections;

    private final QueueSender queueSender;

    public ServerPro(QueueSender queueSender) {
        this.queueSender = queueSender;
    }

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            isServerStart = true;
            System.out.println("ServerStart");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    void acceptServer() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new Thread(new ServerThread(socket)).start();
            } catch (IOException e) {
                System.err.println(" No connection with server");
                e.printStackTrace();
            }
        }
    }

    public void deleteConection(String id) {
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

    private class ServerThread implements Runnable {

        private final Socket socket;

        public ServerThread(Socket socket) {
            this.socket = socket;
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
                if (queueSender.checkSubscriberId(userId)) {
                    try {
                        conection.send(new Message(MessageType.USER_INFO, " WE CHECK Your SUBSCRIBE"));
                        conection.send(queueSender.sendResult(userId));
                        conection.send(new Message(MessageType.DISABLE_USER, " WE handed Result"));
                        deleteConection(userId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        conection.send(new Message(MessageType.USER_INFO, "No SUBSCRIBER WITH ID"));
                        conection.send(new Message(MessageType.REFUSING, "REFUSE"));
                        deleteConection(userId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Message message = conection.receive();
                    if (message.getTypeMessage() == MessageType.DISABLE_USER) {
                        deleteConection(userId);
                    }
                } catch (Exception e) {

                }
            }
        }

        @Override
        public void run() {
            try {
                Conection conection = new Conection(socket);
                Message messageId = requestAddSubscriber(conection);
                communicateWithClient(conection, messageId);
            } catch (IOException e) {
                System.err.println(" ERROR during Communicate Server and Subscriber \n");
                e.printStackTrace();
            }
        }
    }
}


