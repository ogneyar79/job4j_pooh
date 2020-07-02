package model.broker;

import model.HandlerWithJson;
import model.message.MessageB;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BrokerMessage implements IBroker, Closeable {

    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    private final HandlerWithJson handler;


    private Queue<MessageB> firstIn = new ConcurrentLinkedQueue();

    private ConcurrentHashMap<String, ConcurrentLinkedQueue<MessageB>> topicMap = new ConcurrentHashMap();
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<MessageB>> queue = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> topicSubscriber = new ConcurrentHashMap<>();

    private volatile boolean changerTopic = false;

    public BrokerMessage(ServerSocket server, HandlerWithJson handler) {
        this.handler = handler;
        try {
            this.socket = server.accept();
            this.reader = createReader();
            this.writer = createWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeLine(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }

    private BufferedWriter createWriter() throws IOException {
        return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    private BufferedReader createReader() throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
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

    public void addNewTopic(String topic) {
        topicMap.put(topic, new ConcurrentLinkedQueue<MessageB>());
        topicSubscriber.put(topic, new ConcurrentLinkedQueue<String>());
    }

    public void setChangerTopic(boolean changerTopic) {
        this.changerTopic = changerTopic;
    }

    public boolean isChangerTopic() {
        return changerTopic;
    }

    public HandlerWithJson getHandler() {
        return handler;
    }

    public Queue<MessageB> getFirstIn() {
        return firstIn;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<MessageB>> getTopicMap() {
        return this.topicMap;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<MessageB>> getQueue() {
        return queue;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> getTopicSubscriber() {
        return topicSubscriber;
    }
}
