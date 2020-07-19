package model.broker;

import
        model.HandlerWithJson;
import model.connection.Message;
import model.connection.MessageType;
import model.message.MessageB;

public class TopicSender implements IBrokerSender {

    // String topic and queue different message

    private final SubscriberStore subscriberStore;

    public TopicSender(SubscriberStore subscriberStore) {
        this.subscriberStore = subscriberStore;
    }

    public boolean checkSubscriberId(String id) {
        return subscriberStore.getSubscribers().containsKey(id);
    }

    //checking message from mailBox
    public boolean checkMessage(String id) {
        return !subscriberStore.getMailBoxes().get(id).isEmpty();
    }

    private MessageB getMessageFromQueue(String id) {
        return subscriberStore.getMessage(id);
    }

    private String getJSon(String id) {
        MessageB message = getMessageFromQueue(id);
        return new HandlerWithJson().konvertJson(message);
    }

    @Override
    public Message sendResult(String id) {
        return checkMessage(id) ? new Message(MessageType.JSON, getJSon(id)) : new Message(MessageType.USER_INFO, " NO SIBSCRIBE NOW");
    }
}
