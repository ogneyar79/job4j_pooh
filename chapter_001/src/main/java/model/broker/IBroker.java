package model.broker;

import model.message.MessageB;

public interface IBroker {

    boolean insertFirst(MessageB message);

    boolean hendlMessage(MessageB message);

}
