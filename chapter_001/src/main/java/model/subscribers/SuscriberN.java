package model.subscribers;

import model.connection.Connection;
import model.connection.Message;
import model.connection.MessageType;

import java.io.IOException;
import java.net.Socket;

public class SuscriberN implements ISubscriber {

    private Connection connection;
    private final String id;
    private final String addressServer;
    private final int port;
    private volatile boolean isConnect = false; //флаг отобаржающий состояние подключения клиента  серверу

    public SuscriberN(String id, String addressServer, int port) {
        this.id = id;
        this.addressServer = addressServer;
        this.port = port;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    protected Message connectToServer() {
        if (!isConnect) {
            int count = 0;
            while (count == 100) {
                Socket socket = null;
                try {
                    socket = new Socket(addressServer, port);
                    this.connection = new Connection(socket);
                    Message message = connection.receive();
                    if (message.getTextMessage() != null) {
                        isConnect = true;
                        return message;
                    }
                    count++;
                } catch (IOException e) {
                    System.err.println("Произошла ошибка! Возможно Вы ввели не верный адрес сервера или порт. Попробуйте еще раз");
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return new Message(MessageType.DISABLE_USER, " IT's failed to connect");
    }

    public void workWithServer(Message message) {
        while (true) {
            if (message.getTypeMessage() == MessageType.REQUEST_SUBSCRIBER_ID) {
                send(new Message(MessageType.ID_USED, this.id));
            }
            try {
                Message message1 = connection.receive();
                if (message1.getTypeMessage() == MessageType.JSON) {
                    System.out.println("Successful");
                    onMessage(message1.getTextMessage());
                    return;
                } else {
                    System.out.println("Mistake");
                    onMessage(message1.getTextMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMessage(String messageJson) {
        System.out.println(messageJson);
    }

    @Override
    public void send(Message message) {
        try {
            this.connection.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
