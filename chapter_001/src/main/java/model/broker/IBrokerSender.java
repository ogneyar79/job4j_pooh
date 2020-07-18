package model.broker;

import model.connection.Conection;
import model.connection.Message;
import model.message.MessageB;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface IBrokerSender {
    Message sendResult(String id);
}
