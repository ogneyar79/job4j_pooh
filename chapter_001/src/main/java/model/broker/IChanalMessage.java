package model.broker;

import model.ISubscriber;
import model.message.MessageB;

public interface IChanalMessage {

    void send(String message);
}
