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
        return message.getType().equals("topic") ? topicMap.get(message.getKey()).add(message.getText()) : queue.get(message.getKey()).add(message.getText());
    }

    @Override
    public boolean sendMessage(MessageB message) {
        return false;
    }
}
