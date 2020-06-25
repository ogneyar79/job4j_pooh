package model;

public interface ISubscriber {

    void onMessage(String messageJson);
    void subscribe(String data);

}
