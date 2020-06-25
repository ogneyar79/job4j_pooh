package model.broker;

import model.message.MessageB;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class QueueSender implements IBrokerSender {

    private final BrokerMessage brokerMessage;
    private final ConcurrentHashMap<String, Queue<MessageB>> queue;
    private final ConcurrentHashMap<String, Integer> subscriber;
    IChanalMessage chanal;

    public QueueSender(BrokerMessage brokerMessage, ConcurrentHashMap<String, Integer> subscriber) {
        this.brokerMessage = brokerMessage;
        this.queue = brokerMessage.getQueue();
        this.subscriber = subscriber;
    }

    public void setSubscriber(String subject, Integer id) {
        subscriber.put(subject, id);
    }

    public void prepareSending(){
        if (!queue.isEmpty()) {

        }

    }

    @Override
    public void send(String message) {
        chanal.send( message);
    }
}
