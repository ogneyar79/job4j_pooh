package model;

public interface IBroker {

    boolean insertFirst(MessageB message);

    boolean hendlMessage(MessageB message);

    boolean sendMessage(MessageB message);
}
