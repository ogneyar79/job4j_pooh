package model;

import model.broker.BrokerMessage;
import model.message.MessageB;

import java.net.ServerSocket;

public class BrockerServer {

    private final ServerSocket server;
    private final BrokerMessage broker;

    public BrockerServer(ServerSocket server, BrokerMessage broker) {
        this.server = server;
        this.broker = broker;
    }

    public void excute() {
        new Thread(() -> work(broker));
    }

//    public BrockerServer(int port, HandlerWithJson handler, BrokerMessage broker, ServerSocket server, BrokerMessage broker1) {
//        this.server = server;
//        this.broker = broker1;
//
//        try (ServerSocket server = new ServerSocket(port)) {
//            System.out.println("Server started!");
//            try (BrokerMessage brok = new BrokerMessage(server, handler)) {
//                work(brok);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void work(BrokerMessage broker) {
        while (!this.server.isClosed()) {
            String request = broker.readLine();
            System.out.println("Request : " + request);
            MessageB message = broker.getHandler().parseJson(request);
            broker.insertFirst(message);

            String response = " We got it";
            broker.writeLine(response);

            System.out.println("Response : " + response);
        }
    }
}



