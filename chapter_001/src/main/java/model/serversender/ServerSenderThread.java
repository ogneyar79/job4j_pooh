package model.serversender;

public class ServerSenderThread extends Thread {

    private final ServerWithClient senderClient;

    public ServerSenderThread(ServerWithClient senderClient, int port) {
        this.senderClient = senderClient;
        this.senderClient.startServer(port);
        this.start();
    }
}
