package model.broker;

import model.connection.Message;
import model.message.MessageB;

import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicSender implements IBrokerSender {

    private final BrokerMessage brokerMessage;

    // String topic and queue different message
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<MessageB>> topic;
    private final SubscriberStore subscriberStore;
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> topicSubscribe;


    public TopicSender(BrokerMessage brokerMessage, SubscriberStore subscriberStore) {
        this.brokerMessage = brokerMessage;
        this.subscriberStore = subscriberStore;
        this.topic = this.brokerMessage.getTopicMap();
        topicSubscribe = brokerMessage.getTopicSubscriber();

    }

    public void setSubscriber(String id, String subject) {
        subscriberStore.addSubscriber(id, subject);
        topicSubscribe.get(subject).add(id);
    }

    public boolean searchNewMessage() {
        if (brokerMessage.isChangerTopic()) {
            Set<String> keys = topic.keySet();      //     list of topic
            Iterator<String> keysIterator = keys.iterator();
            while (keysIterator.hasNext()) {          //         bypass    all topics
                String topicKey = keysIterator.next();
                if (!topic.get(topicKey).isEmpty()) {                    // if have massage
                    Queue<MessageB> messageBQueue = topic.get(topicKey);             // get queue message for our topic
                    ConcurrentLinkedQueue<String> subscreber = topicSubscribe.get(topic);     // get all subscriber which subscribe for topic
                    while (!messageBQueue.isEmpty()) {
                        MessageB message = messageBQueue.poll();
                        for (String sub : subscreber) {
                            subscriberStore.getQueueMailBox(sub).add(message);              // add every subscriber message to his mailBox
                        }
                    }
                }
            }
            brokerMessage.setChangerTopic(false);
            return true;
        }
        return false;
    }

    @Override
    public Message sendResult(String id) {
        return null;
    }
}
