package model;

import model.connection.Message;

public interface ISubscriber {

    void onMessage(String messageJson);

    void send(Message message);

}
