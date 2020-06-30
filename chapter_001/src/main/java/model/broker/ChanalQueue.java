package model.broker;

import model.ISubscriber;
import model.message.MessageB;

import java.net.ServerSocket;
import java.util.concurrent.BlockingDeque;


public class ChanalQueue implements IChanalMessage {

    private final ServerSocket server;
    public void stsrtServer(int port) {

    }

    // id and socket need to be there
    BlockingDeque<ISubscriber> subscribers;

    public ChanalQueue(ServerSocket server) {
        this.server = server;
    }



    @Override
    public void send(String message) {

    }
}
