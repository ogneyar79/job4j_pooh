package model.broker;

import model.message.MessageB;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SubscriberStore {

    // подписчикId - топик
    private final ConcurrentHashMap<String, String> subscriber;


    // подписчик - очерьдь писем
    private final ConcurrentHashMap<String, Queue<MessageB>> mailBoxes;

    public SubscriberStore(ConcurrentHashMap<String, String> subscriber, ConcurrentHashMap<String, Queue<MessageB>> mailBoxes) {
        this.subscriber = subscriber;
        this.mailBoxes = mailBoxes;
    }

    public Queue<MessageB> getQueueMailBox(String idSub) {
        return mailBoxes.get(idSub);
    }


    private void createMailBox(String id) {
        this.mailBoxes.put(id, new ConcurrentLinkedQueue<MessageB>());
    }

    public void addSubscriber(String id, String topic) {
        subscriber.put(id, topic);
        if (!mailBoxes.containsKey(id)) {
            createMailBox(id);
        }
    }


    public void deleteSubscriber(String id) {
        subscriber.remove(id);
        mailBoxes.remove(id);
    }

    public void addMessage(String id, MessageB message) {
        mailBoxes.get(id).add(message);
    }

    // can Null.
    public MessageB getMessage(String id) {
        return mailBoxes.get(id).poll();
    }
}
