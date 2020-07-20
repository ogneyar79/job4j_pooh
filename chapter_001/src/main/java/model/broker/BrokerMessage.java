package model.broker;

import model.message.MessageB;

import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

public class BrokerMessage implements IBroker {


    private Queue<MessageB> firstIn = new ConcurrentLinkedQueue();
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<MessageB>> topicMap = new ConcurrentHashMap();
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<MessageB>> queue = new ConcurrentHashMap();


    private final SubscriberStore subscriberStore;

    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> topicSubscriber; /// topic and QueSubscriber

    private volatile boolean changerTopic = false;

    public BrokerMessage(SubscriberStore subscriberStore, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> topicSubscriber) {
        this.subscriberStore = subscriberStore;
        this.topicSubscriber = topicSubscriber;
    }

    @Override
    public boolean insertFirst(MessageB message) {
        return firstIn.add(message);
    }

    public boolean distribute() {
        return firstIn.isEmpty() ? false : hendlMessage(firstIn.poll());
    }

    @Override
    public boolean hendlMessage(MessageB message) {
        if (message.getType().equals("topic")) {
            if (!topicMap.containsKey(message.getKeyValue())) {
                topicMap.put(message.getKeyValue(), new ConcurrentLinkedQueue<MessageB>());
                this.setChangerTopic(true);
                return topicMap.get(message.getKeyValue()).add(message);
            }
            this.setChangerTopic(true);
            return topicMap.get(message.getKeyValue()).add(message);
        } else {
            if (!queue.containsKey(message.getKeyValue())) {
                queue.put(message.getKeyValue(), new ConcurrentLinkedQueue<MessageB>());
                return queue.get(message.getKeyValue()).add(message);
            }
            return queue.get(message.getKeyValue()).add(message);
        }
    }

    public boolean searchNewMessage() {
        if (this.isChangerTopic()) {
            Set<String> keys = topicMap.keySet();      //     list of topic their names
            Iterator<String> keysIterator = keys.iterator();
            while (keysIterator.hasNext()) {          //         bypass    all topics
                String topicKey = keysIterator.next();
                if (!topicMap.get(topicKey).isEmpty()) {                    // if have massage at queue in this topic
                    Queue<MessageB> messageBQueue = topicMap.get(topicKey);             // get queue message for our topic
                    ConcurrentLinkedQueue<String> subscreber = this.topicSubscriber.get(topicKey);     // get all subscriber which subscribe for topic
                    while (!messageBQueue.isEmpty()) {
                        MessageB message = messageBQueue.poll();
                        for (String sub : subscreber) {
                            subscriberStore.addMessage(sub, message);              // add every subscriber message to his mailBox
                        }
                    }
                }
            }
            this.setChangerTopic(false);
            return true;
        }
        return false;
    }

    // searchNewMessage change here
    public void addMessageToMailbox() {
        if (this.isChangerTopic()) {
            topicMap.keySet().stream().filter(topicKey -> !this.topicMap.get(topicKey).isEmpty())
                    .forEach(topicKey -> Stream.generate(topicMap.get(topicKey)::poll).limit(topicMap.get(topicKey).size())
                            .forEach(messageB -> topicSubscriber.keySet().stream().filter(key -> !topicSubscriber.get(key).isEmpty())
                                    .forEach(key -> topicSubscriber.get(key).stream().forEach(id -> subscriberStore.addMessage(id, messageB)))));
            this.setChangerTopic(false);
        }
    }

    public void setChangerTopic(boolean changerTopic) {
        this.changerTopic = changerTopic;
    }

    public boolean isChangerTopic() {
        return changerTopic;
    }

}
