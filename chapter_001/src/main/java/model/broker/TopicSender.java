package model.broker;

import model.HandlerWithJson;
import model.connection.Message;
import model.connection.MessageType;
import model.message.MessageB;

import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicSender implements IBrokerSender {


    HandlerWithJson model = new HandlerWithJson();

    // String topic and queue different message

    private final SubscriberStore subscriberStore;


    public TopicSender(SubscriberStore subscriberStore) {
        this.subscriberStore = subscriberStore;
    }

    public boolean checkSubscriberId(String id) {
        return subscriberStore.getSubscriber().containsKey(id);
    }

    //checking message from mailBox
    public boolean checkMessage(String id) {
        return !subscriberStore.getMailBoxes().get(id).isEmpty();
    }

    private MessageB getMessageFromQueue(String id) {
        return subscriberStore.getMailBoxes().get(id).poll();
    }

    private String getJSon(String id) {
        MessageB message = getMessageFromQueue(id);
        return model.konvertJson(message);
    }

    @Override
    public Message sendResult(String id) {
        return checkMessage(id) ? new Message(MessageType.JSON, getJSon(id)) : new Message(MessageType.USER_INFO, " NO SIBSCRIBE NOW");
    }
}
