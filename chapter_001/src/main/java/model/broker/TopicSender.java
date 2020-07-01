package model.broker;

import model.connection.Message;
import model.message.MessageB;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class TopicSender implements IBrokerSender {


    private final BrokerMessage brokerMessage;

    // String topic and queue different message
    private final ConcurrentHashMap<String, Queue<MessageB>> topic;
    private final SubscriberStore subscriberStore;


    public TopicSender(BrokerMessage brokerMessage, SubscriberStore subscriberStore) {
        this.brokerMessage = brokerMessage;
        this.subscriberStore = subscriberStore;
        this.topic = this.brokerMessage.getTopicMap();

    }

    public void setSubscriber(String id, String subject) {
        subscriberStore.addSubscriber(id, subject);
    }

    public boolean searchNewMessage() {
         if (brokerMessage.isChangerTopic())
         {

         }
        return false;
    }

    @Override
    public Message sendResult(String id) {
        return null;
    }
}
