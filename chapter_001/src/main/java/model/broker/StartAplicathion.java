package model.broker;

import model.HandlerWithJson;

import model.serversender.ServerSenderThread;
import model.serversender.ServerWithClient;

import java.net.ServerSocket;

public class StartAplicathion {

    private final BrockerServer brockerServer;


    public StartAplicathion(ServerSocket server, BrokerMessage broker) {
        this.brockerServer = new BrockerServer(server, broker);
    }

    public void startAplication(HandlerWithJson handler) {
        brockerServer.excuteDestrebute();
        brockerServer.excute(handler);
    }

    public void startSending(int port, ServerWithClient sender) {
        new ServerSenderThread(sender, port);
    }

}
