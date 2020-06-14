package model;

public class MessageB {

    private final String type;
    private final String key;
    private final String text;

    public MessageB(String type, String key, String text) {
        this.type = type;
        this.key = key;
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }


}
