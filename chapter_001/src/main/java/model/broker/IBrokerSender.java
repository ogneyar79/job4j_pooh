package model.broker;

import model.connection.Message;

public interface IBrokerSender {
    Message sendResult(String id);
}
