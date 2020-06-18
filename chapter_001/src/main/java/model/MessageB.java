package model;

public class MessageB {

    private final String type;
    private final String keyValue;
    private final String textValue;

    public MessageB(String type, String keyValue, String textValue) {
        this.type = type;
        this.keyValue = keyValue;
        this.textValue = textValue;
    }

    public String getType() {
        return type;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public String getTextValue() {
        return textValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageB messageB = (MessageB) o;

        if (type != null ? !type.equals(messageB.type) : messageB.type != null) return false;
        if (keyValue != null ? !keyValue.equals(messageB.keyValue) : messageB.keyValue != null) return false;
        return textValue != null ? textValue.equals(messageB.textValue) : messageB.textValue == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (keyValue != null ? keyValue.hashCode() : 0);
        result = 31 * result + (textValue != null ? textValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MessageB{" +
                "type='" + type + '\'' +
                ", keyValue='" + keyValue + '\'' +
                ", textValue='" + textValue + '\'' +
                '}';
    }
}
