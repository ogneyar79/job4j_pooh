package model.broker;

import model.HandlerWithJson;
import model.connection.Conection;
import model.connection.Message;
import model.connection.MessageType;
import model.message.MessageB;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueSender implements IBrokerSender {

    private final BrokerMessage brokerMessage;
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<MessageB>> queue;
    private final ConcurrentHashMap<String, String> subscriber;
    // IChanalMessage chanal;
    HandlerWithJson model = new HandlerWithJson();

    public QueueSender(BrokerMessage brokerMessage, ConcurrentHashMap<String, String> subscriber) {
        this.brokerMessage = brokerMessage;
        this.queue = brokerMessage.getQueue();
        this.subscriber = subscriber;
    }

    public void setSubscriber(String id, String subject) {
        subscriber.put(id, subject);
    }

    public boolean checkSubscriberId(String id) {
        return subscriber.containsKey(id);
    }

    public boolean checkMessage(String id) {
        String subjectOfSub = subscriber.get(id);
        return !queue.get(subjectOfSub).isEmpty();
    }

    private MessageB getMessageFromQueue(String id) {
        String subjectOfSub = subscriber.get(id);
        return queue.get(subjectOfSub).poll();
    }

    private String getJSon(String id) {
        MessageB message = getMessageFromQueue(id);
        return model.konvertJson(message);
    }


    public Message sendResult(String id) {
        return checkMessage(id) ? new Message(MessageType.JSON, getJSon(id)) : new Message(MessageType.USER_INFO, " NO SIBSCRIBE NOW");
    }


}
