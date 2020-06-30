package model.connection;

import java.io.Serializable;

public class Message implements Serializable {
    private MessageType typeMess;
    private String text;

    public Message(MessageType typeMess, String text) {
        this.typeMess = typeMess;
        this.text = text;
    }

    public Message(MessageType messageType) {
        this.typeMess = messageType;
        this.text = null;
    }

    public String getTextMessage() {
        return text;
    }

    public MessageType getTypeMessage() {
        return typeMess;
    }
}
