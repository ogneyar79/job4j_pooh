package model;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BrokerMessage implements IBroker {


    Queue<MessageB> firstIn = new ConcurrentLinkedQueue();

    ConcurrentHashMap<String, Queue<String>> topicMap = new ConcurrentHashMap();
    ConcurrentHashMap<String, Queue<String>> queue = new ConcurrentHashMap();

    @Override
    public boolean insertFirst(MessageB message) {
        return firstIn.add(message);
    }

    public boolean distribute() {
        return firstIn.isEmpty() ? false : hendlMessage(firstIn.poll());
    }

    @Override
    public boolean hendlMessage(MessageB message) {
        return message.getType().equals("topic") ? topicMap.get(message.getKeyValue()).add(message.getTextValue()) : queue.get(message.getKeyValue()).add(message.getTextValue());
    }

    @Override
    public boolean sendMessage(MessageB message) {
        return false;
    }
}
