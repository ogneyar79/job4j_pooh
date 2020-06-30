package model.broker;

import model.connection.Conection;
import model.connection.Message;

public interface IBrokerSender {


    Message sendResult(String id);
}
