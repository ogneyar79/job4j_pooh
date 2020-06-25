package model;

public class Subscriber implements ISubscriber {

    int id;

    public Subscriber(int id) {
        this.id = id;
    }

    @Override
    public void onMessage(String messageJson) {
        System.out.println(messageJson);
    }

    @Override
    public void subscribe(String data) {

    }
}
